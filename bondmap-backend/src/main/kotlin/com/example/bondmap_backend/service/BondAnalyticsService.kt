package com.example.bondmap_backend.service

import com.example.bondmap_backend.domain.Bond
import com.example.bondmap_backend.dto.BondAnalyticsResponse
import com.example.bondmap_backend.dto.ScenarioResponse
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.repository.BondPriceRepository
import com.example.bondmap_backend.repository.BondRepository
import com.example.bondmap_backend.repository.KeyRateRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.max

@Service
class BondAnalyticsService(
    private val bondRepository: BondRepository,
    private val bondPriceRepository: BondPriceRepository,
    private val keyRateRepository: KeyRateRepository
) {

    fun getAnalytics(bondId: Long): BondAnalyticsResponse {
        val bond = bondRepository.findById(bondId)
            .orElseThrow { BondNotFoundException(bondId) }

        val price = bondPriceRepository
            .findTopByBondIdOrderByPriceDateDesc(bondId)
            ?.price

        val currentYield = price?.let { currentYield(bond, it) }
        val keyRate = keyRateRepository.findTopByOrderByRateDateDesc()
        val spread = if (currentYield != null && keyRate != null) {
            currentYield - keyRate.rate
        } else null

        return BondAnalyticsResponse(
            ticker = bond.ticker,
            currentPrice = price,
            currentYield = currentYield,
            keyRate = keyRate?.rate,
            keyRateDate = keyRate?.rateDate,
            spreadToKeyRate = spread,
            approxDurationYears = approxDurationYears(bond)
        )
    }

    fun runScenario(bondId: Long, shockBp: Int): ScenarioResponse {
        val bond = bondRepository.findById(bondId)
            .orElseThrow { BondNotFoundException(bondId) }

        val price = bondPriceRepository
            .findTopByBondIdOrderByPriceDateDesc(bondId)
            ?.price

        val duration = approxDurationYears(bond)
        val deltaYield = shockBp / 10_000.0

        val change = if (price != null && duration != null) {
            -duration * deltaYield * price
        } else null

        val newPrice = if (price != null && change != null) price + change else null
        val changePct = if (price != null && change != null && price != 0.0) {
            change / price * 100
        } else null

        return ScenarioResponse(
            ticker = bond.ticker,
            shockBp = shockBp,
            currentPrice = price,
            estimatedNewPrice = newPrice,
            estimatedPriceChange = change,
            estimatedPriceChangePercent = changePct,
            approxDurationYears = duration,
            note = "Учебная оценка: ΔP ≈ -D × Δy × P, где D — упрощённая дюрация до погашения."
        )
    }

    private fun currentYield(bond: Bond, price: Double): Double {
        val annualCouponIncome = bond.nominal * bond.couponRate / 100
        return annualCouponIncome / price * 100
    }

    /**
     * Упрощённая дюрация: доля лет до погашения.
     * Для купонной облигации берём 75% от срока как грубый proxy Macaulay duration.
     */
    private fun approxDurationYears(bond: Bond): Double? {
        val maturity = bond.maturityDate ?: return null
        val today = LocalDate.now()
        if (!maturity.isAfter(today)) return 0.0
        val years = ChronoUnit.DAYS.between(today, maturity) / 365.25
        return max(years * 0.75, 0.1)
    }
}

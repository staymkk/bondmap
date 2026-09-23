package com.example.bondmap_backend.service

import com.example.bondmap_backend.domain.Bond
import com.example.bondmap_backend.domain.BondPrice
import com.example.bondmap_backend.dto.BondAnalyticsResponse
import com.example.bondmap_backend.dto.BondDetailsResponse
import com.example.bondmap_backend.dto.BondSearchResponse
import com.example.bondmap_backend.dto.BondSummaryResponse
import com.example.bondmap_backend.dto.MarketDataResponse
import com.example.bondmap_backend.dto.PricePoint
import com.example.bondmap_backend.dto.ScenarioResponse
import com.example.bondmap_backend.dto.YieldResponse
import com.example.bondmap_backend.repository.BondPriceRepository
import com.example.bondmap_backend.repository.KeyRateRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BondValuationService(
    private val bondPriceRepository: BondPriceRepository,
    private val keyRateRepository: KeyRateRepository
) {

    data class Snapshot(
        val price: BondPrice?,
        val metrics: BondMetrics,
        val marketDate: LocalDate
    )

    fun snapshot(bond: Bond): Snapshot {
        val latest = bond.id?.let { bondPriceRepository.findTopByBondIdOrderByPriceDateDesc(it) }
        val marketDate = latest?.priceDate ?: LocalDate.now()
        val priceValue = latest?.close()
        val metrics = if (priceValue != null) {
            BondCalculator.metrics(bond, priceValue, marketDate)
        } else {
            BondMetrics(null, null, null, null, null)
        }
        return Snapshot(latest, metrics, marketDate)
    }

    fun latestBaseRate() = keyRateRepository.findTopByOrderByRateDateDesc()

    fun toPricePoint(price: BondPrice): PricePoint {
        val close = price.close()
        return PricePoint(
            price = close,
            date = price.priceDate,
            open = price.open(),
            high = price.high(),
            low = price.low(),
            close = close,
            volume = price.volume,
            source = price.source.ifBlank { BondCalculator.SIMULATION_SOURCE }
        )
    }

    fun toMarketData(snapshot: Snapshot): MarketDataResponse {
        val close = snapshot.price?.close()
        return MarketDataResponse(
            price = close,
            volume = snapshot.price?.volume,
            quotedYield = snapshot.metrics.ytmPercent,
            marketDate = snapshot.price?.priceDate,
            source = snapshot.price?.source?.ifBlank { BondCalculator.SIMULATION_SOURCE }
                ?: BondCalculator.SIMULATION_SOURCE
        )
    }

    fun toSummary(bond: Bond): BondSummaryResponse {
        val snap = snapshot(bond)
        return BondSummaryResponse(
            id = bond.id!!,
            ticker = bond.ticker,
            isin = bond.isin.ifBlank { bond.ticker },
            name = bond.name,
            currency = bond.currency,
            type = bond.bondType,
            currentPrice = snap.price?.close(),
            couponRate = bond.couponRate,
            ytm = snap.metrics.ytmPercent,
            currentYield = snap.metrics.currentYieldPercent,
            maturityDate = bond.maturityDate,
            source = snap.price?.source ?: BondCalculator.SIMULATION_SOURCE
        )
    }

    fun toSearch(bond: Bond): BondSearchResponse {
        val snap = snapshot(bond)
        return BondSearchResponse(
            id = bond.id!!,
            ticker = bond.ticker,
            isin = bond.isin.ifBlank { bond.ticker },
            name = bond.name,
            currency = bond.currency,
            couponRate = bond.couponRate,
            currentPrice = snap.price?.close(),
            ytm = snap.metrics.ytmPercent,
            currentYield = snap.metrics.currentYieldPercent,
            maturityDate = bond.maturityDate
        )
    }

    fun toDetails(bond: Bond, history: List<BondPrice>): BondDetailsResponse {
        val snap = snapshot(bond)
        val annualCouponIncome = bond.nominal * bond.couponRate / 100
        return BondDetailsResponse(
            id = bond.id!!,
            ticker = bond.ticker,
            isin = bond.isin.ifBlank { bond.ticker },
            name = bond.name,
            type = bond.bondType,
            typeLabel = BondCalculator.typeLabel(bond.bondType),
            currency = bond.currency,
            nominal = bond.nominal,
            currentPrice = snap.price?.close(),
            couponRate = bond.couponRate,
            couponFrequency = BondCalculator.couponFrequencyPerYear(bond.couponPeriodDays),
            couponPeriodDays = bond.couponPeriodDays,
            maturityDate = bond.maturityDate,
            annualCouponIncome = annualCouponIncome,
            currentYield = snap.metrics.currentYieldPercent,
            ytm = snap.metrics.ytmPercent,
            marketData = toMarketData(snap),
            priceHistory = history
                .sortedBy { it.priceDate }
                .map { toPricePoint(it) }
        )
    }

    fun toAnalytics(bond: Bond): BondAnalyticsResponse {
        val snap = snapshot(bond)
        val base = latestBaseRate()
        val spread = if (snap.metrics.ytmPercent != null && base != null) {
            snap.metrics.ytmPercent - base.rate
        } else null
        return BondAnalyticsResponse(
            ticker = bond.ticker,
            isin = bond.isin.ifBlank { bond.ticker },
            currentPrice = snap.price?.close(),
            ytm = snap.metrics.ytmPercent,
            currentYield = snap.metrics.currentYieldPercent,
            baseRate = base?.rate,
            baseRateDate = base?.rateDate,
            spreadToBaseRate = spread,
            modifiedDuration = snap.metrics.modifiedDuration,
            macaulayDuration = snap.metrics.macaulayDuration,
            convexity = snap.metrics.convexity,
            keyRate = base?.rate,
            keyRateDate = base?.rateDate,
            spreadToKeyRate = spread,
            approxDurationYears = snap.metrics.modifiedDuration
        )
    }

    fun toScenario(bond: Bond, shockBp: Int): ScenarioResponse {
        val snap = snapshot(bond)
        val price = snap.price?.close()
        val duration = snap.metrics.modifiedDuration
        val estimate = if (price != null && duration != null) {
            BondCalculator.estimatePriceByDuration(price, duration, shockBp)
        } else null

        return ScenarioResponse(
            ticker = bond.ticker,
            shockBp = shockBp,
            currentPrice = price,
            estimatedNewPrice = estimate?.newPrice,
            estimatedPriceChange = estimate?.deltaPrice,
            estimatedPriceChangePercent = estimate?.deltaPercent,
            modifiedDuration = duration,
            deltaY = estimate?.deltaY,
            formula = "ΔP ≈ -D_mod × Δy × P",
            note = "Учебная оценка через модифицированную дюрацию. " +
                "Базовая ставка рынка в расчёт не входит. " +
                "Историческая динамика цены не связана с этим сценарием.",
            approxDurationYears = duration
        )
    }

    fun toYield(bond: Bond): YieldResponse {
        val snap = snapshot(bond)
        return YieldResponse(
            ticker = bond.ticker,
            isin = bond.isin.ifBlank { bond.ticker },
            nominal = bond.nominal,
            currentPrice = snap.price?.close(),
            couponRate = bond.couponRate,
            annualCouponIncome = bond.nominal * bond.couponRate / 100,
            currentYield = snap.metrics.currentYieldPercent,
            ytm = snap.metrics.ytmPercent
        )
    }
}

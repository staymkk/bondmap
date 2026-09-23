package com.example.bondmap_backend.service

import com.example.bondmap_backend.domain.Bond
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.pow

data class CashFlow(
    val date: LocalDate,
    val timeYears: Double,
    val amount: Double
)

data class BondMetrics(
    val ytmPercent: Double?,
    val currentYieldPercent: Double?,
    val macaulayDuration: Double?,
    val modifiedDuration: Double?,
    val convexity: Double?
)

data class ScenarioEstimate(
    val deltaY: Double,
    val deltaPrice: Double,
    val newPrice: Double,
    val deltaPercent: Double
)

object BondCalculator {

    const val SIMULATION_SOURCE = "SIMULATION"

    fun couponFrequencyPerYear(couponPeriodDays: Int?): Int {
        val days = couponPeriodDays ?: 182
        return when {
            days <= 45 -> 12
            days <= 120 -> 4
            days <= 200 -> 2
            else -> 1
        }
    }

    fun typeLabel(type: String?): String = when (type?.uppercase()) {
        "GOVERNMENT" -> "Государственная облигация"
        "MUNICIPAL" -> "Муниципальная облигация"
        "EUROBOND" -> "Еврооблигация"
        else -> "Корпоративная облигация"
    }

    fun inferType(name: String, currency: String): String = when {
        name.startsWith("ОФЗ", ignoreCase = true) -> "GOVERNMENT"
        currency.equals("USD", ignoreCase = true) || currency.equals("EUR", ignoreCase = true) -> "EUROBOND"
        else -> "CORPORATE"
    }

    fun metrics(bond: Bond, price: Double, settlementDate: LocalDate): BondMetrics {
        val annualCoupon = bond.nominal * bond.couponRate / 100.0
        val currentYield = if (price != 0.0) annualCoupon / price * 100.0 else null
        val flows = cashFlows(bond, settlementDate)
        if (flows.isEmpty() || price <= 0.0) {
            return BondMetrics(null, currentYield, null, null, null)
        }

        val ytm = solveYtm(price, flows)
            ?: return BondMetrics(null, currentYield, null, null, null)
        val mac = macaulayDuration(price, ytm, flows)
        val modified = mac / (1.0 + ytm)
        val conv = convexity(price, ytm, flows)

        return BondMetrics(
            ytmPercent = ytm * 100.0,
            currentYieldPercent = currentYield,
            macaulayDuration = mac,
            modifiedDuration = modified,
            convexity = conv
        )
    }

    fun cashFlows(bond: Bond, settlement: LocalDate): List<CashFlow> {
        val maturity = bond.maturityDate ?: return emptyList()
        if (!maturity.isAfter(settlement)) return emptyList()

        val freq = couponFrequencyPerYear(bond.couponPeriodDays)
        val months = (12 / freq).coerceAtLeast(1)
        val couponPayment = bond.nominal * bond.couponRate / 100.0 / freq

        val dates = ArrayList<LocalDate>()
        var date = maturity
        while (!date.isBefore(settlement) && dates.size < 240) {
            dates.add(date)
            date = date.minusMonths(months.toLong())
        }
        dates.sort()

        return dates.map { flowDate ->
            val isLast = flowDate == maturity
            val amount = couponPayment + if (isLast) bond.nominal else 0.0
            val years = ChronoUnit.DAYS.between(settlement, flowDate) / 365.25
            CashFlow(flowDate, years, amount)
        }.filter { it.timeYears > 0.0 }
    }

    fun presentValue(ytm: Double, flows: List<CashFlow>): Double =
        flows.sumOf { it.amount / (1.0 + ytm).pow(it.timeYears) }

    fun macaulayDuration(price: Double, ytm: Double, flows: List<CashFlow>): Double {
        if (price == 0.0) return 0.0
        return flows.sumOf { flow ->
            flow.timeYears * flow.amount / (1.0 + ytm).pow(flow.timeYears)
        } / price
    }

    fun convexity(price: Double, ytm: Double, flows: List<CashFlow>): Double {
        if (price == 0.0) return 0.0
        return flows.sumOf { flow ->
            flow.timeYears * (flow.timeYears + 1.0) * flow.amount /
                (1.0 + ytm).pow(flow.timeYears + 2.0)
        } / price
    }

    fun solveYtm(price: Double, flows: List<CashFlow>, guess: Double = 0.12): Double? {
        var y = guess
        repeat(80) {
            val pv = presentValue(y, flows)
            val diff = pv - price
            if (abs(diff) < 1e-7) return y
            val deriv = flows.sumOf { flow ->
                -flow.timeYears * flow.amount / (1.0 + y).pow(flow.timeYears + 1.0)
            }
            if (abs(deriv) < 1e-12) return null
            y -= diff / deriv
            y = y.coerceIn(-0.99, 5.0)
        }
        return if (abs(presentValue(y, flows) - price) < 0.05) y else null
    }

    fun estimatePriceByDuration(
        price: Double,
        modifiedDuration: Double,
        shockBp: Int
    ): ScenarioEstimate {
        val deltaY = shockBp / 10_000.0
        val deltaPrice = -modifiedDuration * deltaY * price
        return ScenarioEstimate(
            deltaY = deltaY,
            deltaPrice = deltaPrice,
            newPrice = price + deltaPrice,
            deltaPercent = if (price != 0.0) deltaPrice / price * 100.0 else 0.0
        )
    }
}

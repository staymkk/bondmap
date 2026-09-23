package com.example.bondmap.data

data class BondDto(
    val id: Long,
    val ticker: String,
    val isin: String? = null,
    val name: String,
    val type: String? = null,
    val typeLabel: String? = null,
    val nominal: Double,
    val couponRate: Double,
    val couponFrequency: Int? = null,
    val maturityDate: String?,
    val currency: String,
    val couponPeriodDays: Int?
)

data class BondSummaryDto(
    val id: Long,
    val ticker: String,
    val isin: String? = null,
    val name: String,
    val currency: String,
    val type: String? = null,
    val currentPrice: Double?,
    val couponRate: Double? = null,
    val ytm: Double? = null,
    val currentYield: Double?,
    val maturityDate: String?,
    val source: String? = null
) {
    fun displayYield(): Double? = ytm ?: currentYield
    fun displayIsin(): String = isin?.ifBlank { ticker } ?: ticker
}

data class BondSearchDto(
    val id: Long,
    val ticker: String,
    val isin: String? = null,
    val name: String,
    val currency: String,
    val couponRate: Double? = null,
    val currentPrice: Double? = null,
    val ytm: Double? = null,
    val currentYield: Double?,
    val maturityDate: String? = null
) {
    fun displayYield(): Double? = ytm ?: currentYield
    fun displayIsin(): String = isin?.ifBlank { ticker } ?: ticker
}

data class BondDetailsDto(
    val id: Long? = null,
    val ticker: String,
    val isin: String? = null,
    val name: String,
    val type: String? = null,
    val typeLabel: String? = null,
    val currency: String,
    val nominal: Double,
    val currentPrice: Double?,
    val couponRate: Double,
    val couponFrequency: Int? = null,
    val couponPeriodDays: Int? = null,
    val maturityDate: String? = null,
    val annualCouponIncome: Double,
    val currentYield: Double?,
    val ytm: Double? = null,
    val marketData: MarketDataDto? = null,
    val priceHistory: List<PricePointDto>
) {
    fun displayIsin(): String = isin?.ifBlank { ticker } ?: ticker
}

data class MarketDataDto(
    val price: Double? = null,
    val volume: Double? = null,
    val quotedYield: Double? = null,
    val marketDate: String? = null,
    val source: String? = null
)

data class PricePointDto(
    val price: Double,
    val date: String,
    val open: Double? = null,
    val high: Double? = null,
    val low: Double? = null,
    val close: Double? = null,
    val volume: Double? = null,
    val source: String? = null
) {
    fun closePrice(): Double = close ?: price
    fun openPrice(previousClose: Double? = null): Double = open ?: previousClose ?: closePrice()
    fun highPrice(): Double = high ?: maxOf(openPrice(), closePrice())
    fun lowPrice(): Double = low ?: minOf(openPrice(), closePrice())
}

data class BondAnalyticsDto(
    val ticker: String,
    val isin: String? = null,
    val currentPrice: Double?,
    val ytm: Double? = null,
    val currentYield: Double?,
    val baseRate: Double? = null,
    val baseRateDate: String? = null,
    val spreadToBaseRate: Double? = null,
    val modifiedDuration: Double? = null,
    val macaulayDuration: Double? = null,
    val convexity: Double? = null,
    val keyRate: Double? = null,
    val keyRateDate: String? = null,
    val spreadToKeyRate: Double? = null,
    val approxDurationYears: Double? = null
) {
    fun displayYtm(): Double? = ytm ?: currentYield
    fun displayBaseRate(): Double? = baseRate ?: keyRate
    fun displaySpread(): Double? = spreadToBaseRate ?: spreadToKeyRate
    fun displayModifiedDuration(): Double? = modifiedDuration ?: approxDurationYears
}

data class ScenarioDto(
    val ticker: String,
    val shockBp: Int,
    val currentPrice: Double?,
    val estimatedNewPrice: Double?,
    val estimatedPriceChange: Double?,
    val estimatedPriceChangePercent: Double?,
    val modifiedDuration: Double? = null,
    val deltaY: Double? = null,
    val formula: String? = null,
    val note: String,
    val approxDurationYears: Double? = null
) {
    fun duration(): Double? = modifiedDuration ?: approxDurationYears
}

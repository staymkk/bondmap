package com.example.bondmap.data

data class BondDto(
    val id: Long,
    val ticker: String,
    val name: String,
    val nominal: Double,
    val couponRate: Double,
    val maturityDate: String?,
    val currency: String,
    val couponPeriodDays: Int?
)

data class BondSummaryDto(
    val id: Long,
    val ticker: String,
    val name: String,
    val currency: String,
    val currentPrice: Double?,
    val currentYield: Double?,
    val maturityDate: String?
)

data class BondSearchDto(
    val id: Long,
    val ticker: String,
    val name: String,
    val currency: String,
    val currentYield: Double?
)

data class BondDetailsDto(
    val ticker: String,
    val name: String,
    val currency: String,
    val nominal: Double,
    val currentPrice: Double?,
    val couponRate: Double,
    val annualCouponIncome: Double,
    val currentYield: Double?,
    val priceHistory: List<PricePointDto>
)

data class PricePointDto(
    val price: Double,
    val date: String
)

data class BondAnalyticsDto(
    val ticker: String,
    val currentPrice: Double?,
    val currentYield: Double?,
    val keyRate: Double?,
    val keyRateDate: String?,
    val spreadToKeyRate: Double?,
    val approxDurationYears: Double?
)

data class ScenarioDto(
    val ticker: String,
    val shockBp: Int,
    val currentPrice: Double?,
    val estimatedNewPrice: Double?,
    val estimatedPriceChange: Double?,
    val estimatedPriceChangePercent: Double?,
    val approxDurationYears: Double?,
    val note: String
)

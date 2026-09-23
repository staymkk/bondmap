package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Расчёт текущей доходности облигации")
data class YieldResponse(

    @Schema(description = "Биржевой тикер", example = "26243")
    val ticker: String,

    @Schema(description = "ISIN", example = "RU000A1038V6")
    val isin: String,

    @Schema(description = "Номинальная стоимость", example = "1000.0")
    val nominal: Double,

    @Schema(description = "Текущая цена облигации", example = "945.80", nullable = true)
    val currentPrice: Double?,

    @Schema(description = "Купонная ставка в процентах годовых", example = "14.0")
    val couponRate: Double,

    @Schema(description = "Годовой доход от купонов", example = "140.0")
    val annualCouponIncome: Double,

    @Schema(description = "Текущая доходность = годовой купон / текущая цена * 100", example = "14.80", nullable = true)
    val currentYield: Double?,

    @Schema(description = "Доходность к погашению (YTM), %", example = "14.80", nullable = true)
    val ytm: Double?
)

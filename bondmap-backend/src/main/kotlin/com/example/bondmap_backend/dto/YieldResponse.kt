package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema


@Schema(
    description = "Расчёт текущей доходности облигации"
)
data class YieldResponse(

    @Schema(
        description = "Биржевой тикер",
        example = "RU000A102345"
    )
    val ticker: String,


    @Schema(
        description = "Номинальная стоимость",
        example = "1000.0"
    )
    val nominal: Double,


    @Schema(
        description = "Текущая цена облигации",
        example = "978.5",
        nullable = true
    )
    val currentPrice: Double?,


    @Schema(
        description = "Купонная ставка в процентах годовых",
        example = "15.5"
    )
    val couponRate: Double,


    @Schema(
        description = "Годовой доход от купонов",
        example = "155.0"
    )
    val annualCouponIncome: Double,


    @Schema(
        description = "Текущая доходность = годовой купон / текущая цена * 100",
        example = "15.84",
        nullable = true
    )
    val currentYield: Double?
)
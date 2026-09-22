package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate


@Schema(
    description = "Подробная информация об облигации"
)
data class BondDetailsResponse(

    @Schema(
        description = "Биржевой тикер",
        example = "RU000A102345"
    )
    val ticker: String,


    @Schema(
        description = "Название выпуска",
        example = "ОФЗ-ПД 26238"
    )
    val name: String,


    @Schema(
        description = "Валюта облигации",
        example = "RUB"
    )
    val currency: String,


    @Schema(
        description = "Номинальная стоимость облигации",
        example = "1000.0"
    )
    val nominal: Double,


    @Schema(
        description = "Текущая рыночная цена",
        example = "978.5",
        nullable = true
    )
    val currentPrice: Double?,


    @Schema(
        description = "Размер купона в процентах годовых",
        example = "15.5"
    )
    val couponRate: Double,


    @Schema(
        description = "Годовой доход от купонов",
        example = "155.0"
    )
    val annualCouponIncome: Double,


    @Schema(
        description = "Текущая доходность облигации",
        example = "15.82",
        nullable = true
    )
    val currentYield: Double?,


    @Schema(
        description = "История изменения цены",
    )
    val priceHistory: List<PricePoint>
)


@Schema(
    description = "Точка изменения цены облигации"
)
data class PricePoint(

    @Schema(
        description = "Цена облигации",
        example = "978.5"
    )
    val price: Double,


    @Schema(
        description = "Дата изменения цены",
        example = "2026-09-20"
    )
    val date: LocalDate
)
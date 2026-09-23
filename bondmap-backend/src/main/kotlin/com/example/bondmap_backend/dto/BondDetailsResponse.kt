package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Подробная информация об облигации")
data class BondDetailsResponse(

    @Schema(description = "Идентификатор", example = "1")
    val id: Long,

    @Schema(description = "Биржевой тикер", example = "26243")
    val ticker: String,

    @Schema(description = "ISIN", example = "RU000A1038V6")
    val isin: String,

    @Schema(description = "Название выпуска", example = "ОФЗ-ПД 26243")
    val name: String,

    @Schema(description = "Код типа", example = "GOVERNMENT")
    val type: String,

    @Schema(description = "Тип облигации", example = "Государственная облигация")
    val typeLabel: String,

    @Schema(description = "Валюта облигации", example = "RUB")
    val currency: String,

    @Schema(description = "Номинальная стоимость облигации", example = "1000.0")
    val nominal: Double,

    @Schema(description = "Текущая рыночная цена (симуляция)", example = "945.80", nullable = true)
    val currentPrice: Double?,

    @Schema(description = "Размер купона в процентах годовых", example = "14.0")
    val couponRate: Double,

    @Schema(description = "Число купонных выплат в год", example = "2")
    val couponFrequency: Int,

    @Schema(description = "Период купона, дни", example = "182")
    val couponPeriodDays: Int?,

    @Schema(description = "Дата погашения", example = "2038-05-19")
    val maturityDate: LocalDate?,

    @Schema(description = "Годовой доход от купонов", example = "140.0")
    val annualCouponIncome: Double,

    @Schema(description = "Текущая доходность (купон / цена)", example = "14.80", nullable = true)
    val currentYield: Double?,

    @Schema(description = "Доходность к погашению, %", example = "14.80", nullable = true)
    val ytm: Double?,

    @Schema(description = "Игровые рыночные данные")
    val marketData: MarketDataResponse,

    @Schema(description = "История изменения цены")
    val priceHistory: List<PricePoint>
)

@Schema(description = "Игровые рыночные данные")
data class MarketDataResponse(
    @Schema(example = "945.80")
    val price: Double?,
    @Schema(example = "1250000")
    val volume: Double?,
    @Schema(description = "Котируемая доходность (симуляция / YTM)", example = "14.80")
    val quotedYield: Double?,
    @Schema(example = "2026-09-15")
    val marketDate: LocalDate?,
    @Schema(example = "SIMULATION")
    val source: String = "SIMULATION"
)

@Schema(description = "Точка / свеча изменения цены облигации")
data class PricePoint(

    @Schema(description = "Цена закрытия", example = "945.80")
    val price: Double,

    @Schema(description = "Дата", example = "2026-09-15")
    val date: LocalDate,

    @Schema(example = "943.10")
    val open: Double,

    @Schema(example = "952.00")
    val high: Double,

    @Schema(example = "941.00")
    val low: Double,

    @Schema(example = "945.80")
    val close: Double,

    @Schema(example = "1250000")
    val volume: Double?,

    @Schema(example = "SIMULATION")
    val source: String = "SIMULATION"
)

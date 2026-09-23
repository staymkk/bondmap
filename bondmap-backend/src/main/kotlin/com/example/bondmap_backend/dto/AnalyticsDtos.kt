package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class KeyRateResponse(
    @Schema(example = "1")
    val id: Long,
    @Schema(example = "14.0")
    val rate: Double,
    @Schema(example = "2026-09-15")
    val rateDate: LocalDate
)

data class CreateKeyRateRequest(
    @field:Positive
    @Schema(example = "14.0")
    val rate: Double,
    @Schema(example = "2026-09-15")
    val rateDate: LocalDate
)

@Schema(description = "Расчётные показатели облигации")
data class BondAnalyticsResponse(
    val ticker: String,
    val isin: String,
    val currentPrice: Double?,
    @Schema(description = "Доходность к погашению (YTM), %")
    val ytm: Double?,
    @Schema(description = "Текущая доходность (годовой купон / цена), %")
    val currentYield: Double?,
    @Schema(description = "Базовая ставка рынка, % — ориентир, не входит в расчёт цены")
    val baseRate: Double?,
    val baseRateDate: LocalDate?,
    @Schema(description = "Спред: YTM − базовая ставка, п.п.")
    val spreadToBaseRate: Double?,
    @Schema(description = "Модифицированная дюрация, лет")
    val modifiedDuration: Double?,
    @Schema(description = "Дюрация Маколея, лет")
    val macaulayDuration: Double?,
    @Schema(description = "Выпуклость")
    val convexity: Double?,
    @Schema(description = "Совместимость: прежнее имя базовой ставки")
    val keyRate: Double?,
    val keyRateDate: LocalDate?,
    val spreadToKeyRate: Double?,
    @Schema(description = "Совместимость: то же, что modifiedDuration")
    val approxDurationYears: Double?
)

data class ScenarioResponse(
    val ticker: String,
    val shockBp: Int,
    val currentPrice: Double?,
    val estimatedNewPrice: Double?,
    val estimatedPriceChange: Double?,
    val estimatedPriceChangePercent: Double?,
    @Schema(description = "Модифицированная дюрация")
    val modifiedDuration: Double?,
    val deltaY: Double?,
    val formula: String,
    val note: String,
    @Schema(description = "Совместимость")
    val approxDurationYears: Double?
)

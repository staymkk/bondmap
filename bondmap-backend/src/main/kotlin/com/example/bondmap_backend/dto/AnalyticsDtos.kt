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

data class BondAnalyticsResponse(
    val ticker: String,
    val currentPrice: Double?,
    val currentYield: Double?,
    val keyRate: Double?,
    val keyRateDate: LocalDate?,
    @Schema(description = "Спред: текущая доходность минус ключевая ставка, п.п.")
    val spreadToKeyRate: Double?,
    @Schema(description = "Упрощённая дюрация (лет) для сценариев")
    val approxDurationYears: Double?
)

data class ScenarioResponse(
    val ticker: String,
    val shockBp: Int,
    val currentPrice: Double?,
    val estimatedNewPrice: Double?,
    val estimatedPriceChange: Double?,
    val estimatedPriceChangePercent: Double?,
    val approxDurationYears: Double?,
    val note: String
)

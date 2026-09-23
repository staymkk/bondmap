package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive
import java.time.LocalDate

@Schema(description = "Добавление новой цены облигации")
data class CreateBondPriceRequest(

    @Schema(description = "Цена закрытия облигации", example = "945.80")
    @field:Positive
    val price: Double,

    @Schema(description = "Дата цены", example = "2026-09-15")
    val priceDate: LocalDate,

    val open: Double? = null,
    val high: Double? = null,
    val low: Double? = null,
    val close: Double? = null,
    val volume: Double? = null
)

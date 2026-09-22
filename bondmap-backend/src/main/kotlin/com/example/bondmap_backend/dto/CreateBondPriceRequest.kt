package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive
import java.time.LocalDate

@Schema(
    description = "Добавление новой цены облигации"
)
data class CreateBondPriceRequest(

    @Schema(
        description = "Цена облигации",
        example = "978.5"
    )
    @field:Positive
    val price: Double,

    @Schema(
        description = "Дата цены",
        example = "2026-09-20"
    )
    val priceDate: LocalDate
)
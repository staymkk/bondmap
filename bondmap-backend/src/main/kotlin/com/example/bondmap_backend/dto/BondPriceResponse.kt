package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(
    description = "Историческая цена облигации"
)
data class BondPriceResponse(

    @Schema(
        description = "ID записи цены",
        example = "1"
    )
    val id: Long,

    @Schema(
        description = "Цена облигации",
        example = "978.5"
    )
    val price: Double,

    @Schema(
        description = "Дата изменения цены",
        example = "2026-09-20"
    )
    val priceDate: LocalDate
)
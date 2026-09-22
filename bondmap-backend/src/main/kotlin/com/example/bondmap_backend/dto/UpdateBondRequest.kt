package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDate


data class UpdateBondRequest(

    @field:NotBlank
    @Schema(
        example = "RU000A1038V6"
    )
    val ticker: String,


    @field:NotBlank
    @Schema(
        example = "Газпромбанк БО-001Р-25"
    )
    val name: String,


    @field:Positive
    @Schema(
        example = "1000"
    )
    val nominal: Double,


    @field:PositiveOrZero
    @Schema(
        example = "15.5"
    )
    val couponRate: Double,


    @Schema(
        example = "2030-12-15"
    )
    val maturityDate: LocalDate?,


    @Schema(
        example = "RUB"
    )
    val currency: String,


    @Schema(
        example = "182"
    )
    val couponPeriodDays: Int?,


    @field:PositiveOrZero
    @Schema(
        example = "1015.4"
    )
    val currentPrice: Double?
)
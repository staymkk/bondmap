package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDate

data class UpdateBondRequest(

    @field:NotBlank
    @Schema(example = "26243")
    val ticker: String,

    @Schema(example = "RU000A1038V6")
    val isin: String? = null,

    @field:NotBlank
    @Schema(example = "ОФЗ-ПД 26243")
    val name: String,

    @Schema(example = "GOVERNMENT")
    val type: String? = null,

    @field:Positive
    @Schema(example = "1000")
    val nominal: Double,

    @field:PositiveOrZero
    @Schema(example = "14.0")
    val couponRate: Double,

    @Schema(example = "2038-05-19")
    val maturityDate: LocalDate?,

    @Schema(example = "RUB")
    val currency: String,

    @Schema(example = "182")
    val couponPeriodDays: Int?
)

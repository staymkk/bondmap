package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDate

@Schema(description = "Данные для создания облигации")
data class CreateBondRequest(

    @Schema(description = "Биржевой тикер", example = "26243")
    @field:NotBlank
    val ticker: String,

    @Schema(description = "ISIN", example = "RU000A1038V6")
    val isin: String? = null,

    @Schema(description = "Название выпуска", example = "ОФЗ-ПД 26243")
    @field:NotBlank
    val name: String,

    @Schema(description = "Тип: GOVERNMENT, CORPORATE, EUROBOND, MUNICIPAL", example = "GOVERNMENT")
    val type: String? = null,

    @Schema(description = "Номинальная стоимость", example = "1000.0")
    @field:Positive
    val nominal: Double,

    @Schema(description = "Купонная ставка", example = "14.0")
    @field:PositiveOrZero
    val couponRate: Double,

    @Schema(description = "Дата погашения", example = "2038-05-19")
    val maturityDate: LocalDate?,

    @Schema(description = "Валюта", example = "RUB")
    val currency: String = "RUB",

    @Schema(description = "Период выплаты купона в днях", example = "182")
    val couponPeriodDays: Int?
)

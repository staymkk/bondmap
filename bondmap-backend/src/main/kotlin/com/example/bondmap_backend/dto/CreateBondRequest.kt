package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDate


@Schema(
    description = "Данные для создания облигации"
)
data class CreateBondRequest(

    @Schema(
        description = "Биржевой тикер",
        example = "RU000A102345"
    )
    @field:NotBlank
    val ticker: String,

    @Schema(
        description = "Название выпуска",
        example = "ОФЗ-ПД 26238"
    )
    @field:NotBlank
    val name: String,

    @Schema(
        description = "Номинальная стоимость",
        example = "1000.0"
    )
    @field:Positive
    val nominal: Double,

    @Schema(
        description = "Купонная ставка",
        example = "15.5"
    )
    @field:PositiveOrZero
    val couponRate: Double,

    @Schema(
        description = "Дата погашения",
        example = "2039-07-15"
    )
    val maturityDate: LocalDate?,

    @Schema(
        description = "Валюта",
        example = "RUB"
    )
    val currency: String = "RUB",

    @Schema(
        description = "Период выплаты купона в днях",
        example = "182"
    )
    val couponPeriodDays: Int?,

    @Schema(
        description = "Текущая цена",
        example = "978.5"
    )
    val currentPrice: Double?
)
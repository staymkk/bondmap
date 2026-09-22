package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class BondResponse(

    @Schema(
        description = "Уникальный идентификатор облигации",
        example = "1"
    )
    val id: Long,


    @Schema(
        description = "Биржевой тикер",
        example = "RU000A102345"
    )
    val ticker: String,


    @Schema(
        description = "Название выпуска",
        example = "ОФЗ-ПД 26238"
    )
    val name: String,


    @Schema(
        description = "Номинальная стоимость одной облигации",
        example = "1000.0"
    )
    val nominal: Double,


    @Schema(
        description = "Размер купона в процентах годовых",
        example = "15.5"
    )
    val couponRate: Double,


    @Schema(
        description = "Дата погашения",
        example = "2039-07-15"
    )
    val maturityDate: LocalDate?,


    @Schema(
        description = "Валюта выпуска",
        example = "RUB"
    )
    val currency: String,


    @Schema(
        description = "Период между купонами в днях",
        example = "182"
    )
    val couponPeriodDays: Int?
)
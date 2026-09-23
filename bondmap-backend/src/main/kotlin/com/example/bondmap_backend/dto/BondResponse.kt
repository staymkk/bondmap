package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class BondResponse(

    @Schema(description = "Уникальный идентификатор облигации", example = "1")
    val id: Long,

    @Schema(description = "Короткий тикер", example = "26243")
    val ticker: String,

    @Schema(description = "ISIN", example = "RU000A1038V6")
    val isin: String,

    @Schema(description = "Название выпуска", example = "ОФЗ-ПД 26243")
    val name: String,

    @Schema(description = "Тип облигации", example = "GOVERNMENT")
    val type: String,

    @Schema(description = "Человекочитаемый тип", example = "Государственная облигация")
    val typeLabel: String,

    @Schema(description = "Номинальная стоимость одной облигации", example = "1000.0")
    val nominal: Double,

    @Schema(description = "Размер купона в процентах годовых", example = "14.0")
    val couponRate: Double,

    @Schema(description = "Число купонных выплат в год", example = "2")
    val couponFrequency: Int,

    @Schema(description = "Дата погашения", example = "2038-05-19")
    val maturityDate: LocalDate?,

    @Schema(description = "Валюта выпуска", example = "RUB")
    val currency: String,

    @Schema(description = "Период между купонами в днях", example = "182")
    val couponPeriodDays: Int?
)

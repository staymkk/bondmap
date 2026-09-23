package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Облигация в результатах поиска")
data class BondSearchResponse(

    @Schema(description = "Идентификатор облигации", example = "1")
    val id: Long,

    @Schema(description = "Тикер", example = "26243")
    val ticker: String,

    @Schema(description = "ISIN", example = "RU000A1038V6")
    val isin: String,

    @Schema(description = "Название выпуска", example = "ОФЗ-ПД 26243")
    val name: String,

    @Schema(description = "Валюта", example = "RUB")
    val currency: String,

    @Schema(description = "Купон, %", example = "14.0")
    val couponRate: Double,

    @Schema(description = "Цена", example = "945.80", nullable = true)
    val currentPrice: Double?,

    @Schema(description = "YTM, %", example = "14.80", nullable = true)
    val ytm: Double?,

    @Schema(description = "Текущая доходность", example = "14.80", nullable = true)
    val currentYield: Double?,

    @Schema(description = "Дата погашения", example = "2038-05-19", nullable = true)
    val maturityDate: LocalDate?
)

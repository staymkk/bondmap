package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Краткая информация об облигации для списка")
data class BondSummaryResponse(

    @Schema(description = "Уникальный идентификатор облигации", example = "1")
    val id: Long,

    @Schema(description = "Короткий тикер", example = "26243")
    val ticker: String,

    @Schema(description = "ISIN", example = "RU000A1038V6")
    val isin: String,

    @Schema(description = "Название выпуска", example = "ОФЗ-ПД 26243")
    val name: String,

    @Schema(description = "Валюта выпуска", example = "RUB")
    val currency: String,

    @Schema(description = "Тип облигации", example = "GOVERNMENT")
    val type: String,

    @Schema(description = "Последняя известная цена (симуляция)", example = "945.80", nullable = true)
    val currentPrice: Double?,

    @Schema(description = "Купон, % годовых", example = "14.0")
    val couponRate: Double,

    @Schema(description = "Доходность к погашению (YTM), %", example = "14.80", nullable = true)
    val ytm: Double?,

    @Schema(description = "Текущая доходность (купон / цена), %", example = "14.80", nullable = true)
    val currentYield: Double?,

    @Schema(description = "Дата погашения", example = "2038-05-19", nullable = true)
    val maturityDate: LocalDate?,

    @Schema(description = "Источник котировки", example = "SIMULATION")
    val source: String = "SIMULATION"
)

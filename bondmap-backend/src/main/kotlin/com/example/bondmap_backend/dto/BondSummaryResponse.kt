package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(
    description = "Краткая информация об облигации для списка"
)
data class BondSummaryResponse(

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
        description = "Валюта выпуска",
        example = "RUB"
    )
    val currency: String,

    @Schema(
        description = "Последняя известная цена",
        example = "978.5",
        nullable = true
    )
    val currentPrice: Double?,

    @Schema(
        description = "Текущая доходность облигации",
        example = "15.82",
        nullable = true
    )
    val currentYield: Double?,

    @Schema(
        description = "Дата погашения",
        example = "2039-07-15",
        nullable = true
    )
    val maturityDate: LocalDate?
)
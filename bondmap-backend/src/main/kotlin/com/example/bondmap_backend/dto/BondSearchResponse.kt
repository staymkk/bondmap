package com.example.bondmap_backend.dto

import io.swagger.v3.oas.annotations.media.Schema


@Schema(
    description = "Облигация в результатах поиска"
)
data class BondSearchResponse(

    @Schema(
        description = "Идентификатор облигации",
        example = "1"
    )
    val id: Long,

    @Schema(
        description = "Биржевой тикер облигации",
        example = "RU000A000006"
    )
    val ticker: String,


    @Schema(
        description = "Название выпуска",
        example = "ОФЗ-ПД 26238"
    )
    val name: String,


    @Schema(
        description = "Валюта",
        example = "RUB"
    )
    val currency: String,


    @Schema(
        description = "Текущая доходность",
        example = "15.82",
        nullable = true
    )
    val currentYield: Double?
)
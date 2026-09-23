package com.example.bondmap_backend.controller

import com.example.bondmap_backend.service.BondSearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/bonds")
@Tag(
    name = "Bond Search",
    description = "Поиск и фильтрация облигаций"
)
class BondSearchController(
    private val bondSearchService: BondSearchService
) {

    @Operation(
        summary = "Поиск облигаций",
        description = """
            Поиск по названию, ISIN и тикеру.
            Фильтры: валюта, ISIN, название, диапазон даты погашения, доходность.
        """
    )
    @GetMapping("/search")
    fun search(

        @Parameter(description = "Строка поиска: название, ISIN или тикер", example = "26243")
        @RequestParam(required = false)
        query: String?,

        @Parameter(description = "Фильтр по валюте", example = "RUB")
        @RequestParam(required = false)
        currency: String?,

        @Parameter(description = "Фильтр по ISIN", example = "RU000")
        @RequestParam(required = false)
        isin: String?,

        @Parameter(description = "Фильтр по названию", example = "ОФЗ")
        @RequestParam(required = false)
        name: String?,

        @Parameter(description = "Дата погашения от", example = "2027-01-01")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        maturityFrom: LocalDate?,

        @Parameter(description = "Дата погашения до", example = "2038-12-31")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        maturityTo: LocalDate?,

        @Parameter(description = "Минимальная доходность YTM", example = "10.0")
        @RequestParam(required = false)
        minYield: Double?,

        @Parameter(description = "Максимальная доходность YTM", example = "20.0")
        @RequestParam(required = false)
        maxYield: Double?,

        @Parameter(description = "yield_desc / yield_asc", example = "yield_desc")
        @RequestParam(required = false)
        sort: String?

    ) = bondSearchService.search(
        query,
        currency,
        isin,
        name,
        maturityFrom,
        maturityTo,
        minYield,
        maxYield,
        sort
    )
}

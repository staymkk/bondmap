package com.example.bondmap_backend.controller

import com.example.bondmap_backend.service.BondSearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*


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
            Поиск облигаций с фильтрацией.
            
            Можно:
            - выбрать валюту
            - задать диапазон доходности
            - отсортировать результаты
            
            Сортировка:
            - yield_desc — сначала высокая доходность
            - yield_asc — сначала низкая доходность
        """
    )
    @GetMapping("/search")
    fun search(

        @Parameter(
            description = "Фильтр по валюте",
            example = "RUB"
        )
        @RequestParam(required = false)
        currency: String?,


        @Parameter(
            description = "Минимальная доходность",
            example = "10.0"
        )
        @RequestParam(required = false)
        minYield: Double?,


        @Parameter(
            description = "Максимальная доходность",
            example = "20.0"
        )
        @RequestParam(required = false)
        maxYield: Double?,


        @Parameter(
            description = """
                Сортировка:
                yield_desc - по убыванию доходности
                yield_asc - по возрастанию доходности
            """,
            example = "yield_desc"
        )
        @RequestParam(required = false)
        sort: String?

    ) =
        bondSearchService.search(
            currency,
            minYield,
            maxYield,
            sort
        )
}
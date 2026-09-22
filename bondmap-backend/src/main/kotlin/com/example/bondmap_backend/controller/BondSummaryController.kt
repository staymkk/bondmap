package com.example.bondmap_backend.controller

import com.example.bondmap_backend.dto.BondSummaryResponse
import com.example.bondmap_backend.service.BondSummaryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bonds")
@Tag(
    name = "Bond Summary",
    description = "Краткая информация по облигациям"
)
class BondSummaryController(
    private val bondSummaryService: BondSummaryService
) {

    @Operation(
        summary = "Получить список облигаций с основными показателями",
        description = """
            Возвращает краткую информацию по всем облигациям:
            - тикер
            - название
            - валюта
            - текущая цена
            - текущая доходность
            - дата погашения
        """
    )
    @GetMapping("/summary")
    fun getSummary(): List<BondSummaryResponse> {
        return bondSummaryService.getAll()
    }
}
package com.example.bondmap_backend.controller

import com.example.bondmap_backend.dto.BondAnalyticsResponse
import com.example.bondmap_backend.dto.ScenarioResponse
import com.example.bondmap_backend.service.BondAnalyticsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bonds")
@Tag(name = "Bond Analytics", description = "Спред к ключевой ставке и сценарии")
class BondAnalyticsController(
    private val bondAnalyticsService: BondAnalyticsService
) {

    @Operation(
        summary = "Аналитика облигации",
        description = "Доходность, ключевая ставка и спред (yield − key rate)"
    )
    @GetMapping("/{id}/analytics")
    fun analytics(@PathVariable id: Long): BondAnalyticsResponse =
        bondAnalyticsService.getAnalytics(id)

    @Operation(
        summary = "Сценарий шока ставки",
        description = "Оценка изменения цены при шоке ключевой/доходности в базисных пунктах"
    )
    @GetMapping("/{id}/scenario")
    fun scenario(
        @PathVariable id: Long,
        @Parameter(description = "Шок в б.п. (+100 = рост доходности на 1 п.п.)", example = "100")
        @RequestParam(defaultValue = "100") shockBp: Int
    ): ScenarioResponse = bondAnalyticsService.runScenario(id, shockBp)
}

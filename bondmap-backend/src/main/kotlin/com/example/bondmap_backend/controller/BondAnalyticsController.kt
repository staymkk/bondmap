package com.example.bondmap_backend.controller

import com.example.bondmap_backend.dto.BondAnalyticsResponse
import com.example.bondmap_backend.dto.ScenarioResponse
import com.example.bondmap_backend.service.BondAnalyticsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bonds")
@Tag(name = "Bond Analytics", description = "YTM, дюрация, спред к базовой ставке и сценарии доходности")
class BondAnalyticsController(
    private val bondAnalyticsService: BondAnalyticsService
) {

    @Operation(
        summary = "Аналитика облигации",
        description = "YTM, модифицированная дюрация, базовая ставка рынка и спред (YTM − BaseRate). " +
            "Базовая ставка — только ориентир, в расчёт цены не входит."
    )
    @GetMapping("/{id}/analytics")
    fun analytics(@PathVariable id: Long): BondAnalyticsResponse =
        bondAnalyticsService.getAnalytics(id)

    @Operation(
        summary = "Сценарий изменения доходности",
        description = "Оценка новой цены через модифицированную дюрацию: ΔP ≈ -D_mod × Δy × P"
    )
    @GetMapping("/{id}/scenario")
    fun scenario(
        @PathVariable id: Long,
        @Parameter(description = "Изменение требуемой доходности в б.п. (+100 = +1 п.п.)", example = "100")
        @RequestParam(defaultValue = "100") shockBp: Int
    ): ScenarioResponse = bondAnalyticsService.runScenario(id, shockBp)
}

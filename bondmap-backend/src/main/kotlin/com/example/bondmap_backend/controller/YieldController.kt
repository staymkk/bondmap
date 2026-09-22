package com.example.bondmap_backend.controller

import com.example.bondmap_backend.dto.YieldResponse
import com.example.bondmap_backend.service.YieldService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bonds")
@Tag(
    name = "Bond Yield",
    description = "Расчёт доходности облигаций"
)
class YieldController(
    private val yieldService: YieldService
) {


    @Operation(
        summary = "Рассчитать текущую доходность облигации",
        description = """
            Возвращает:
            - номинальную стоимость
            - текущую цену
            - купонную ставку
            - годовой купонный доход
            - текущую доходность
            
            Формула:
            текущая доходность = годовой купон / текущая цена * 100
        """
    )
    @ApiResponse(
        responseCode = "200",
        description = "Доходность рассчитана"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Облигация не найдена"
    )
    @GetMapping("/{id}/yield")
    fun getYield(
        @PathVariable id: Long
    ): YieldResponse {

        return yieldService.calculateYield(id)
    }
}
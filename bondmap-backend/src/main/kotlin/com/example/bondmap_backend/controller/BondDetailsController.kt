package com.example.bondmap_backend.controller

import com.example.bondmap_backend.dto.BondDetailsResponse
import com.example.bondmap_backend.service.BondDetailsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bonds")
@Tag(
    name = "Bond Details",
    description = "Подробная информация об облигациях"
)
class BondDetailsController(
    private val bondDetailsService: BondDetailsService
) {


    @Operation(
        summary = "Получить детали облигации",
        description = """
            Возвращает расширенную информацию:
            - параметры выпуска
            - текущую цену
            - купонный доход
            - текущую доходность
            - историю изменения цены
        """
    )
    @ApiResponse(
        responseCode = "200",
        description = "Данные успешно получены"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Облигация не найдена"
    )
    @GetMapping("/{id}/details")
    fun getDetails(
        @PathVariable id: Long
    ): BondDetailsResponse {

        return bondDetailsService.getDetails(id)
    }
}
package com.example.bondmap_backend.controller

import com.example.bondmap_backend.dto.BondPriceResponse
import com.example.bondmap_backend.dto.CreateBondPriceRequest
import com.example.bondmap_backend.service.BondPriceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bonds")
@Tag(
    name = "Bond Price",
    description = "История цен облигаций"
)
class BondPriceController(
    private val bondPriceService: BondPriceService
) {

    @Operation(
        summary = "Добавить цену облигации",
        description = "Сохраняет новую цену облигации в историю"
    )
    @PostMapping("/{id}/prices")
    fun createPrice(
        @Parameter(
            description = "ID облигации",
            example = "1"
        )
        @PathVariable id: Long,

        @Valid
        @RequestBody request: CreateBondPriceRequest
    ): BondPriceResponse {

        return bondPriceService.create(id, request)
    }


    @Operation(
        summary = "Получить историю цен",
        description = "Возвращает все исторические цены выбранной облигации"
    )
    @GetMapping("/{id}/prices")
    fun getPrices(
        @Parameter(
            description = "ID облигации",
            example = "1"
        )
        @PathVariable id: Long
    ): List<BondPriceResponse> {

        return bondPriceService.getHistory(id)
    }
}
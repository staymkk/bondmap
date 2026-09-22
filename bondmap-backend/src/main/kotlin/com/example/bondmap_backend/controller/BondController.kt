package com.example.bondmap_backend.controller

import com.example.bondmap_backend.dto.BondResponse
import com.example.bondmap_backend.dto.CreateBondRequest
import com.example.bondmap_backend.dto.UpdateBondRequest
import com.example.bondmap_backend.service.BondService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/bonds")
@Tag(
    name = "Bond Management",
    description = "CRUD операции с облигациями"
)
class BondController(
    private val bondService: BondService
) {


    @Operation(
        summary = "Получить список облигаций",
        description = "Возвращает все облигации из базы данных"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Список облигаций получен"
    )
    @GetMapping
    fun getBonds(): List<BondResponse> {

        return bondService.getAll()
    }



    @Operation(
        summary = "Получить облигацию по ID",
        description = "Возвращает информацию о конкретном выпуске"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Облигация найдена"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Облигация не найдена"
            )
        ]
    )
    @GetMapping("/{id}")
    fun getBond(
        @PathVariable id: Long
    ): BondResponse {

        return bondService.getById(id)
    }



    @Operation(
        summary = "Создать облигацию",
        description = "Добавляет новый выпуск облигации"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Облигация создана"
    )
    @PostMapping
    fun createBond(
        @Valid
        @RequestBody request: CreateBondRequest
    ): BondResponse {

        return bondService.create(request)
    }



    @Operation(
        summary = "Обновить облигацию",
        description = "Изменяет параметры существующей облигации"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Облигация обновлена"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Облигация не найдена"
            )
        ]
    )
    @PutMapping("/{id}")
    fun updateBond(
        @PathVariable id: Long,
        @RequestBody request: UpdateBondRequest
    ): BondResponse {

        return bondService.update(id, request)
    }



    @Operation(
        summary = "Удалить облигацию",
        description = "Удаляет облигацию по идентификатору"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Облигация удалена"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Облигация не найдена"
            )
        ]
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(
        code = org.springframework.http.HttpStatus.NO_CONTENT
    )
    fun deleteBond(
        @PathVariable id: Long
    ) {

        bondService.delete(id)
    }
}
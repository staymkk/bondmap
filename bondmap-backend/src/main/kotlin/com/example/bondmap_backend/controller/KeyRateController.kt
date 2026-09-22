package com.example.bondmap_backend.controller

import com.example.bondmap_backend.dto.CreateKeyRateRequest
import com.example.bondmap_backend.dto.KeyRateResponse
import com.example.bondmap_backend.service.KeyRateService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/key-rates")
@Tag(name = "Key Rate", description = "Ключевая ставка (учебный ряд)")
class KeyRateController(
    private val keyRateService: KeyRateService
) {

    @Operation(summary = "Последняя ключевая ставка")
    @GetMapping("/latest")
    fun latest(): KeyRateResponse? = keyRateService.getLatest()

    @Operation(summary = "История ключевой ставки")
    @GetMapping
    fun history(): List<KeyRateResponse> = keyRateService.getHistory()

    @Operation(summary = "Добавить значение ключевой ставки")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateKeyRateRequest): KeyRateResponse =
        keyRateService.create(request)
}

package com.example.bondmap_backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(
    name = "System",
    description = "Системные проверки приложения"
)
class HealthController {

    @Operation(
        summary = "Проверка состояния сервера",
        description = "Возвращает статус работы приложения"
    )
    @GetMapping("/health")
    fun health(): String {
        return "OK"
    }
}
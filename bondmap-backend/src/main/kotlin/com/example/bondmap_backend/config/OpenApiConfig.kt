package com.example.bondmap_backend.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("BondMap API")
                    .version("1.0")
                    .description(
                        """
                        API для анализа облигаций.
                        
                        Возможности:
                        - управление облигациями
                        - хранение истории цен
                        - расчёт текущей доходности
                        - поиск и фильтрация выпусков
                        """.trimIndent()
                    )
            )
    }
}
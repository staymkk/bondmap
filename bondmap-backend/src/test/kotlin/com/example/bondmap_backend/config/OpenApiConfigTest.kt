package com.example.bondmap_backend.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OpenApiConfigTest {

    @Test
    fun `open api title is BondMap`() {
        val api = OpenApiConfig().customOpenAPI()
        assertEquals("BondMap API", api.info.title)
        assertEquals("1.0", api.info.version)
    }
}

package com.example.bondmap_backend

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.boot.test.context.SpringBootTest

// Full context needs PostgreSQL. Set RUN_INTEGRATION_TESTS=true to enable.
// Unit API tests in controller package do not need a database.
@EnabledIfEnvironmentVariable(named = "RUN_INTEGRATION_TESTS", matches = "true")
@SpringBootTest
class BondmapBackendApplicationTests {

    @Test
    fun contextLoads() {
    }
}

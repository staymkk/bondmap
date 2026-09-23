package com.example.bondmap_backend.domain

import com.example.bondmap_backend.BondFixtures.bond
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BondPriceTest {

    @Test
    fun `ohlc falls back to stored price`() {
        val quote = BondPrice(
            id = 1L,
            bond = bond(),
            price = 100.0,
            priceDate = LocalDate.of(2026, 9, 15),
            openPrice = null,
            highPrice = null,
            lowPrice = null,
            closePrice = null,
            volume = null,
            source = ""
        )
        assertEquals(100.0, quote.close())
        assertEquals(100.0, quote.open())
        assertEquals(100.0, quote.high())
        assertEquals(100.0, quote.low())
    }

    @Test
    fun `high and low use open and close when missing`() {
        val quote = BondPrice(
            id = 2L,
            bond = bond(),
            price = 90.0,
            priceDate = LocalDate.of(2026, 9, 15),
            openPrice = 110.0,
            highPrice = null,
            lowPrice = null,
            closePrice = 90.0,
            volume = null,
            source = "SIMULATION"
        )
        assertEquals(110.0, quote.high())
        assertEquals(90.0, quote.low())
    }
}

package com.example.bondmap_backend.service

import com.example.bondmap_backend.BondFixtures.bond
import com.example.bondmap_backend.BondFixtures.price
import com.example.bondmap_backend.dto.CreateBondPriceRequest
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.repository.BondPriceRepository
import com.example.bondmap_backend.repository.BondRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.Optional

class BondPriceServiceTest {

    private val bonds = mock<BondRepository>()
    private val prices = mock<BondPriceRepository>()
    private val service = BondPriceService(bonds, prices)
    private val ofz = bond()

    @Test
    fun `create fills missing ohlc from close`() {
        whenever(bonds.findById(1L)).thenReturn(Optional.of(ofz))
        whenever(prices.save(any())).thenAnswer { invocation ->
            val saved = invocation.getArgument<com.example.bondmap_backend.domain.BondPrice>(0)
            price(
                bond = saved.bond,
                id = 9L,
                close = saved.price,
                date = saved.priceDate,
                open = saved.openPrice,
                high = saved.highPrice,
                low = saved.lowPrice,
                volume = saved.volume,
                source = saved.source
            )
        }

        val created = service.create(
            1L,
            CreateBondPriceRequest(
                price = 950.0,
                priceDate = LocalDate.of(2026, 9, 20)
            )
        )

        assertEquals(9L, created.id)
        assertEquals(950.0, created.close)
        assertEquals(950.0, created.open)
        assertEquals(950.0, created.high)
        assertEquals(950.0, created.low)
        assertEquals("SIMULATION", created.source)
    }

    @Test
    fun `create keeps explicit candle`() {
        whenever(bonds.findById(1L)).thenReturn(Optional.of(ofz))
        whenever(prices.save(any())).thenAnswer { invocation ->
            val saved = invocation.getArgument<com.example.bondmap_backend.domain.BondPrice>(0)
            price(
                id = 11L,
                close = saved.closePrice ?: saved.price,
                open = saved.openPrice,
                high = saved.highPrice,
                low = saved.lowPrice,
                volume = saved.volume
            )
        }

        val created = service.create(
            1L,
            CreateBondPriceRequest(
                price = 945.8,
                priceDate = LocalDate.of(2026, 9, 15),
                open = 940.0,
                high = 960.0,
                low = 930.0,
                close = 945.8,
                volume = 10.0
            )
        )
        assertEquals(940.0, created.open)
        assertEquals(960.0, created.high)
        assertEquals(930.0, created.low)
        assertEquals(10.0, created.volume)
    }

    @Test
    fun `create throws when bond missing`() {
        whenever(bonds.findById(99L)).thenReturn(Optional.empty())
        assertThrows(BondNotFoundException::class.java) {
            service.create(99L, CreateBondPriceRequest(price = 100.0, priceDate = LocalDate.now()))
        }
    }

    @Test
    fun `history maps quotes`() {
        whenever(bonds.existsById(1L)).thenReturn(true)
        whenever(prices.findAllByBondIdOrderByPriceDateAsc(1L)).thenReturn(listOf(price(source = "")))
        val history = service.getHistory(1L)
        assertEquals(1, history.size)
        assertEquals("SIMULATION", history[0].source)
        assertEquals(945.8, history[0].close)
    }

    @Test
    fun `history throws when bond missing`() {
        whenever(bonds.existsById(99L)).thenReturn(false)
        assertThrows(BondNotFoundException::class.java) { service.getHistory(99L) }
    }
}

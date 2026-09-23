package com.example.bondmap_backend.service

import com.example.bondmap_backend.BondFixtures.bond
import com.example.bondmap_backend.BondFixtures.keyRate
import com.example.bondmap_backend.BondFixtures.price
import com.example.bondmap_backend.repository.BondPriceRepository
import com.example.bondmap_backend.repository.KeyRateRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

class BondValuationServiceTest {

    private val prices = mock<BondPriceRepository>()
    private val rates = mock<KeyRateRepository>()
    private val service = BondValuationService(prices, rates)
    private val ofz = bond()

    @Test
    fun `snapshot without id skips price lookup`() {
        val snap = service.snapshot(bond(id = null))
        assertNull(snap.price)
        assertNull(snap.metrics.ytmPercent)
    }

    @Test
    fun `snapshot without price returns empty metrics`() {
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(1L)).thenReturn(null)
        val snap = service.snapshot(ofz)
        assertNull(snap.price)
        assertNull(snap.metrics.ytmPercent)
        assertEquals(LocalDate.now(), snap.marketDate)
    }

    @Test
    fun `snapshot with quote computes ytm and duration`() {
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(1L)).thenReturn(price())
        val snap = service.snapshot(ofz)
        assertEquals(945.8, snap.price!!.close())
        assertNotNull(snap.metrics.ytmPercent)
        assertTrue(snap.metrics.ytmPercent!! > 14.0)
        assertNotNull(snap.metrics.modifiedDuration)
    }

    @Test
    fun `summary search details yield and analytics use latest quote`() {
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(1L)).thenReturn(price())
        whenever(rates.findTopByOrderByRateDateDesc()).thenReturn(keyRate())

        val summary = service.toSummary(ofz)
        assertEquals("RU000A1038V6", summary.isin)
        assertEquals(945.8, summary.currentPrice)
        assertEquals("SIMULATION", summary.source)

        val search = service.toSearch(ofz.copyWithoutIsin())
        assertEquals("26243", search.isin)

        val older = price(date = LocalDate.of(2026, 1, 1), close = 900.0)
        val newer = price(date = LocalDate.of(2026, 9, 15), close = 945.8)
        val details = service.toDetails(ofz, listOf(newer, older))
        assertEquals(140.0, details.annualCouponIncome)
        assertEquals(listOf(older.priceDate, newer.priceDate), details.priceHistory.map { it.date })
        assertEquals("Государственная облигация", details.typeLabel)

        val analytics = service.toAnalytics(ofz)
        assertNotNull(analytics.spreadToBaseRate)
        assertEquals(analytics.spreadToBaseRate, analytics.spreadToKeyRate)
        assertEquals(14.0, analytics.baseRate)

        val yieldDto = service.toYield(ofz)
        assertEquals(140.0, yieldDto.annualCouponIncome)
        assertEquals(945.8, yieldDto.currentPrice)
    }

    @Test
    fun `analytics without key rate has no spread`() {
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(1L)).thenReturn(price())
        whenever(rates.findTopByOrderByRateDateDesc()).thenReturn(null)
        val analytics = service.toAnalytics(ofz)
        assertNull(analytics.spreadToBaseRate)
        assertNull(analytics.baseRate)
    }

    @Test
    fun `scenario estimates new price from duration`() {
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(1L)).thenReturn(price())
        val scenario = service.toScenario(ofz, 100)
        assertEquals(100, scenario.shockBp)
        assertNotNull(scenario.estimatedNewPrice)
        assertTrue(scenario.estimatedNewPrice!! < 945.8)
        assertTrue(scenario.formula.contains("D_mod"))
    }

    @Test
    fun `scenario without quote stays empty`() {
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(1L)).thenReturn(null)
        val scenario = service.toScenario(ofz, 50)
        assertNull(scenario.estimatedNewPrice)
        assertNull(scenario.deltaY)
    }

    @Test
    fun `price point falls back to close and simulation source`() {
        val raw = price(open = null, high = null, low = null, source = " ")
        val point = service.toPricePoint(raw)
        assertEquals(945.8, point.close)
        assertEquals(945.8, point.open)
        assertEquals("SIMULATION", point.source)
    }

    @Test
    fun `latestBaseRate reads repository`() {
        whenever(rates.findTopByOrderByRateDateDesc()).thenReturn(keyRate())
        assertEquals(14.0, service.latestBaseRate()!!.rate)
    }

    private fun com.example.bondmap_backend.domain.Bond.copyWithoutIsin() = bond(isin = "")
}

package com.example.bondmap_backend.service

import com.example.bondmap_backend.BondFixtures.bond
import com.example.bondmap_backend.BondFixtures.keyRate
import com.example.bondmap_backend.BondFixtures.price
import com.example.bondmap_backend.domain.KeyRate
import com.example.bondmap_backend.dto.CreateKeyRateRequest
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.repository.BondPriceRepository
import com.example.bondmap_backend.repository.BondRepository
import com.example.bondmap_backend.repository.KeyRateRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.Optional

class SupportingServicesTest {

    private val bonds = mock<BondRepository>()
    private val prices = mock<BondPriceRepository>()
    private val rates = mock<KeyRateRepository>()
    private val valuation = BondValuationService(prices, rates)
    private val ofz = bond()

    @Test
    fun `details uses quote and sorts history by date`() {
        val older = price(date = LocalDate.of(2026, 1, 1), close = 900.0)
        val newer = price(date = LocalDate.of(2026, 9, 15), close = 945.8)
        whenever(bonds.findById(1L)).thenReturn(Optional.of(ofz))
        whenever(prices.findAllByBondIdOrderByPriceDateAsc(1L)).thenReturn(listOf(newer, older))
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(1L)).thenReturn(newer)

        val details = BondDetailsService(bonds, prices, valuation).getDetails(1L)

        assertEquals(140.0, details.annualCouponIncome)
        assertEquals(945.8, details.currentPrice)
        assertTrue(details.ytm!! > 14.0)
        assertEquals(
            listOf(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 9, 15)),
            details.priceHistory.map { it.date }
        )
    }

    @Test
    fun `details throws if bond is missing`() {
        whenever(bonds.findById(9L)).thenReturn(Optional.empty())
        assertThrows(BondNotFoundException::class.java) {
            BondDetailsService(bonds, prices, valuation).getDetails(9L)
        }
    }

    @Test
    fun `analytics spread is ytm minus key rate`() {
        whenever(bonds.findById(1L)).thenReturn(Optional.of(ofz))
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(1L)).thenReturn(price())
        whenever(rates.findTopByOrderByRateDateDesc()).thenReturn(keyRate(rate = 14.0))

        val analytics = BondAnalyticsService(bonds, valuation).getAnalytics(1L)
        assertEquals(14.0, analytics.baseRate)
        assertEquals(analytics.ytm!! - 14.0, analytics.spreadToBaseRate!!, 1e-9)
    }

    @Test
    fun `scenario of plus 100bp lowers estimated price`() {
        whenever(bonds.findById(1L)).thenReturn(Optional.of(ofz))
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(1L)).thenReturn(price())

        val scenario = BondAnalyticsService(bonds, valuation).runScenario(1L, 100)
        assertEquals(100, scenario.shockBp)
        assertTrue(scenario.estimatedNewPrice!! < 945.8)
        assertEquals(0.01, scenario.deltaY!!, 1e-9)
    }

    @Test
    fun `analytics throws if bond is missing`() {
        whenever(bonds.findById(9L)).thenReturn(Optional.empty())
        assertThrows(BondNotFoundException::class.java) {
            BondAnalyticsService(bonds, valuation).getAnalytics(9L)
        }
        assertThrows(BondNotFoundException::class.java) {
            BondAnalyticsService(bonds, valuation).runScenario(9L, 50)
        }
    }

    @Test
    fun `yield income is nominal times coupon`() {
        whenever(bonds.findById(1L)).thenReturn(Optional.of(ofz))
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(1L)).thenReturn(price())

        val yieldDto = YieldService(bonds, valuation).calculateYield(1L)
        assertEquals(140.0, yieldDto.annualCouponIncome)
        assertEquals(140.0 / 945.8 * 100.0, yieldDto.currentYield!!, 0.01)
    }

    @Test
    fun `summary returns every bond from repository`() {
        val second = bond(id = 2L, ticker = "26238", isin = "RU000A102345", name = "ОФЗ-ПД 26238")
        whenever(bonds.findAll()).thenReturn(listOf(ofz, second))
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(1L)).thenReturn(price())
        whenever(prices.findTopByBondIdOrderByPriceDateDesc(2L)).thenReturn(null)

        val summary = BondSummaryService(bonds, valuation).getAll()
        assertEquals(listOf(1L, 2L), summary.map { it.id })
        assertEquals(945.8, summary[0].currentPrice)
        assertNull(summary[1].currentPrice)
        assertNull(summary[1].ytm)
    }

    @Test
    fun `key rate create persists request fields`() {
        val service = KeyRateService(rates)
        whenever(rates.findTopByOrderByRateDateDesc()).thenReturn(null)
        assertNull(service.getLatest())

        whenever(rates.save(org.mockito.kotlin.any())).thenAnswer { invocation ->
            val saved = invocation.getArgument<KeyRate>(0)
            KeyRate(id = 8L, rate = saved.rate, rateDate = saved.rateDate)
        }

        val created = service.create(CreateKeyRateRequest(15.5, LocalDate.of(2026, 9, 22)))
        val captor = argumentCaptor<KeyRate>()
        verify(rates).save(captor.capture())
        assertEquals(15.5, captor.firstValue.rate)
        assertEquals(LocalDate.of(2026, 9, 22), captor.firstValue.rateDate)
        assertEquals(8L, created.id)
        assertEquals(15.5, created.rate)
    }
}

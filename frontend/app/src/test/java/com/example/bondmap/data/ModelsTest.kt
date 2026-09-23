package com.example.bondmap.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ModelsTest {

    @Test
    fun displayYieldPrefersYtm() {
        val withYtm = BondSummaryDto(
            id = 1,
            ticker = "26243",
            isin = "RU000A1038V6",
            name = "ОФЗ-ПД 26243",
            currency = "RUB",
            currentPrice = 945.8,
            ytm = 14.8,
            currentYield = 14.0,
            maturityDate = "2038-05-19"
        )
        assertEquals(14.8, withYtm.displayYield()!!, 0.0)
        assertEquals("RU000A1038V6", withYtm.displayIsin())
    }

    @Test
    fun displayYieldFallsBackAndIsinFallsBackToTicker() {
        val search = BondSearchDto(
            id = 2,
            ticker = "26238",
            isin = " ",
            name = "OFZ",
            currency = "RUB",
            ytm = null,
            currentYield = 12.1,
            maturityDate = null
        )
        assertEquals(12.1, search.displayYield()!!, 0.0)
        assertEquals("26238", search.displayIsin())
    }

    @Test
    fun displayHelpersFallBackToLegacyFields() {
        val analytics = BondAnalyticsDto(
            ticker = "26243",
            currentPrice = 945.8,
            ytm = null,
            currentYield = 12.1,
            baseRate = null,
            spreadToBaseRate = null,
            modifiedDuration = null,
            keyRate = 14.0,
            spreadToKeyRate = 1.2,
            approxDurationYears = 5.0
        )
        assertEquals(12.1, analytics.displayYtm()!!, 0.0)
        assertEquals(14.0, analytics.displayBaseRate()!!, 0.0)
        assertEquals(1.2, analytics.displaySpread()!!, 0.0)
        assertEquals(5.0, analytics.displayModifiedDuration()!!, 0.0)

        val scenario = ScenarioDto(
            ticker = "26243",
            shockBp = 100,
            currentPrice = 945.8,
            estimatedNewPrice = 900.0,
            estimatedPriceChange = -45.8,
            estimatedPriceChangePercent = -4.8,
            modifiedDuration = null,
            note = "n",
            approxDurationYears = 5.3
        )
        assertEquals(5.3, scenario.duration()!!, 0.0)
    }

    @Test
    fun pricePointFallsBackToClose() {
        val point = PricePointDto(price = 100.0, date = "2026-09-15")
        assertEquals(100.0, point.closePrice(), 0.0)
        assertEquals(100.0, point.openPrice(), 0.0)
        assertNull(point.volume)
    }
}

package com.example.bondmap.ui.components

import com.example.bondmap.data.PricePointDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ChartFilterTest {

    private val history = listOf(
        PricePointDto(price = 100.0, date = "2026-01-01"),
        PricePointDto(price = 101.0, date = "2026-06-01"),
        PricePointDto(price = 102.0, date = "2026-09-01")
    )

    @Test
    fun allRangeKeepsSortedHistory() {
        val filtered = filterHistory(history, PriceChartRange.ALL)
        assertEquals(3, filtered.size)
        assertEquals("2026-01-01", filtered.first().date)
    }

    @Test
    fun monthRangeKeepsOnlyRecentPoints() {
        val filtered = filterHistory(history, PriceChartRange.MONTH)
        assertEquals(1, filtered.size)
        assertEquals("2026-09-01", filtered.single().date)
    }
}

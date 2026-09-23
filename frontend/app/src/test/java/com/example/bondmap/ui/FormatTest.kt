package com.example.bondmap.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class FormatTest {

    @Test
    fun parseIsoAndRuDates() {
        assertEquals(LocalDate.of(2038, 5, 19), parseDate("2038-05-19"))
        assertEquals(LocalDate.of(2027, 1, 1), parseDate("01.01.2027"))
        assertNull(parseDate(" "))
        assertNull(parseDate("not-a-date"))
    }

    @Test
    fun formatHelpers() {
        assertEquals("19.05.2038", formatDate("2038-05-19"))
        assertEquals("19.05", formatChartDate("2038-05-19"))
        assertEquals("—", formatPercent(null))
        assertEquals("+4.09 п.п.", formatSignedPp(4.09).replace(',', '.'))
        assertEquals("945.80", formatNumber(945.8).replace(',', '.'))
    }
}

package com.example.bondmap_backend.service

import com.example.bondmap_backend.domain.Bond
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.math.abs

class BondCalculatorTest {

    @Test
    fun `zero coupon ytm and duration are calculated from cash flows`() {
        val bond = Bond(
            ticker = "ZC",
            name = "Zero",
            nominal = 1000.0,
            couponRate = 0.0,
            maturityDate = LocalDate.of(2028, 1, 1),
            currency = "RUB",
            couponPeriodDays = 365,
            isin = "RU000ZERO001",
            bondType = "GOVERNMENT"
        )
        val settlement = LocalDate.of(2026, 1, 1)
        val price = 1000.0 / 1.1 / 1.1
        val metrics = BondCalculator.metrics(bond, price, settlement)

        assertNotNull(metrics.ytmPercent)
        assertEquals(10.0, metrics.ytmPercent!!, 0.05)
        assertEquals(2.0, metrics.macaulayDuration!!, 0.02)
        assertEquals(2.0 / 1.1, metrics.modifiedDuration!!, 0.02)
    }

    @Test
    fun `par coupon bond ytm equals coupon rate`() {
        val bond = Bond(
            ticker = "26243",
            name = "ОФЗ-ПД 26243",
            nominal = 1000.0,
            couponRate = 14.0,
            maturityDate = LocalDate.of(2028, 5, 19),
            currency = "RUB",
            couponPeriodDays = 365,
            isin = "RU000A1038V6",
            bondType = "GOVERNMENT"
        )
        val settlement = LocalDate.of(2026, 5, 19)
        val metrics = BondCalculator.metrics(bond, 1000.0, settlement)

        assertNotNull(metrics.ytmPercent)
        assertEquals(14.0, metrics.ytmPercent!!, 0.15)
        assertTrue(metrics.modifiedDuration!! > 0.0)
        assertTrue(metrics.convexity!! > 0.0)
    }

    @Test
    fun `scenario uses modified duration formula`() {
        val estimate = BondCalculator.estimatePriceByDuration(
            price = 945.80,
            modifiedDuration = 5.38,
            shockBp = 100
        )
        assertEquals(0.01, estimate.deltaY, 1e-9)
        assertEquals(-5.38 * 0.01 * 945.80, estimate.deltaPrice, 0.01)
        assertEquals(945.80 + estimate.deltaPrice, estimate.newPrice, 0.01)
        assertTrue(abs(estimate.deltaPercent + 5.38) < 0.05)
    }

    @Test
    fun `discount bond ytm is above coupon`() {
        val bond = Bond(
            ticker = "26243",
            name = "ОФЗ-ПД 26243",
            nominal = 1000.0,
            couponRate = 14.0,
            maturityDate = LocalDate.of(2038, 5, 19),
            currency = "RUB",
            couponPeriodDays = 182,
            isin = "RU000A1038V6",
            bondType = "GOVERNMENT"
        )
        val metrics = BondCalculator.metrics(bond, 945.80, LocalDate.of(2026, 9, 15))
        assertNotNull(metrics.ytmPercent)
        assertTrue(metrics.ytmPercent!! > 14.0)
        assertTrue(metrics.modifiedDuration!! > 0.0)
    }

    @Test
    fun `cash flows are empty after maturity`() {
        val bond = Bond(
            ticker = "MAT",
            name = "Matured",
            nominal = 1000.0,
            couponRate = 10.0,
            maturityDate = LocalDate.of(2020, 1, 1),
            currency = "RUB",
            couponPeriodDays = 182
        )
        assertTrue(BondCalculator.cashFlows(bond, LocalDate.of(2026, 1, 1)).isEmpty())
    }

    @Test
    fun `inferType maps OFZ and FX bonds`() {
        assertEquals("GOVERNMENT", BondCalculator.inferType("ОФЗ-ПД 26243", "RUB"))
        assertEquals("EUROBOND", BondCalculator.inferType("Gazprom", "USD"))
        assertEquals("CORPORATE", BondCalculator.inferType("MTS", "RUB"))
        assertEquals("Муниципальная облигация", BondCalculator.typeLabel("MUNICIPAL"))
        assertEquals(2, BondCalculator.couponFrequencyPerYear(182))
        assertEquals(4, BondCalculator.couponFrequencyPerYear(90))
    }
}

package com.example.bondmap_backend.service

import com.example.bondmap_backend.domain.Bond
import com.example.bondmap_backend.dto.BondSearchResponse
import com.example.bondmap_backend.repository.BondRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

class BondSearchServiceTest {

    private val repository = mock<BondRepository>()
    private val valuation = mock<BondValuationService>()
    private val service = BondSearchService(repository, valuation)

    private val ofz = Bond(
        id = 1L,
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
    private val euro = Bond(
        id = 2L,
        ticker = "XS001",
        name = "Eurobond",
        nominal = 1000.0,
        couponRate = 5.0,
        maturityDate = LocalDate.of(2030, 1, 15),
        currency = "USD",
        couponPeriodDays = 182,
        isin = "XS0000000001",
        bondType = "EUROBOND"
    )

    @Test
    fun `filters by currency and sorts by yield descending`() {
        whenever(repository.findAll()).thenReturn(listOf(ofz, euro))
        whenever(valuation.toSearch(ofz)).thenReturn(search(ofz, ytm = 14.8))
        whenever(valuation.toSearch(euro)).thenReturn(search(euro, ytm = 5.2))

        val result = service.search(
            query = null,
            currency = "RUB",
            isin = null,
            name = null,
            maturityFrom = null,
            maturityTo = null,
            minYield = null,
            maxYield = null,
            sort = "yield_desc"
        )

        assertEquals(1, result.size)
        assertEquals("RU000A1038V6", result[0].isin)
    }

    @Test
    fun `filters by yield range and name`() {
        whenever(repository.findAll()).thenReturn(listOf(ofz, euro))
        whenever(valuation.toSearch(ofz)).thenReturn(search(ofz, ytm = 14.8))
        whenever(valuation.toSearch(euro)).thenReturn(search(euro, ytm = 5.2))

        val result = service.search(
            query = "офз",
            currency = null,
            isin = null,
            name = null,
            maturityFrom = LocalDate.of(2035, 1, 1),
            maturityTo = LocalDate.of(2040, 1, 1),
            minYield = 10.0,
            maxYield = 20.0,
            sort = "yield_asc"
        )

        assertEquals(1, result.size)
        assertEquals("26243", result[0].ticker)
    }

    private fun search(bond: Bond, ytm: Double) = BondSearchResponse(
        id = bond.id!!,
        ticker = bond.ticker,
        isin = bond.isin,
        name = bond.name,
        currency = bond.currency,
        couponRate = bond.couponRate,
        currentPrice = 945.8,
        ytm = ytm,
        currentYield = bond.couponRate,
        maturityDate = bond.maturityDate
    )
}

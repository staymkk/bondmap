package com.example.bondmap_backend.service

import com.example.bondmap_backend.domain.Bond
import com.example.bondmap_backend.dto.CreateBondRequest
import com.example.bondmap_backend.dto.UpdateBondRequest
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.repository.BondRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.Optional

class BondServiceTest {

    private val repository = mock<BondRepository>()
    private val service = BondService(repository)

    @Test
    fun `getById throws when bond is missing`() {
        whenever(repository.findById(99L)).thenReturn(Optional.empty())
        assertThrows(BondNotFoundException::class.java) { service.getById(99L) }
    }

    @Test
    fun `create infers government type for OFZ and uses ticker as isin fallback`() {
        whenever(repository.save(any<Bond>())).thenAnswer { invocation ->
            val bond = invocation.getArgument<Bond>(0)
            Bond(
                id = 10L,
                ticker = bond.ticker,
                name = bond.name,
                nominal = bond.nominal,
                couponRate = bond.couponRate,
                maturityDate = bond.maturityDate,
                currency = bond.currency,
                couponPeriodDays = bond.couponPeriodDays,
                isin = bond.isin,
                bondType = bond.bondType
            )
        }

        val created = service.create(
            CreateBondRequest(
                ticker = "26243",
                isin = null,
                name = "ОФЗ-ПД 26243",
                type = null,
                nominal = 1000.0,
                couponRate = 14.0,
                maturityDate = LocalDate.of(2038, 5, 19),
                currency = "RUB",
                couponPeriodDays = 182
            )
        )

        assertEquals(10L, created.id)
        assertEquals("26243", created.isin)
        assertEquals("GOVERNMENT", created.type)
        assertEquals("Государственная облигация", created.typeLabel)
    }

    @Test
    fun `getAll and getById map entities`() {
        val ofz = Bond(
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
        whenever(repository.findAll()).thenReturn(listOf(ofz))
        whenever(repository.findById(1L)).thenReturn(Optional.of(ofz))

        assertEquals(1, service.getAll().size)
        assertEquals("RU000A1038V6", service.getById(1L).isin)
    }

    @Test
    fun `update replaces fields and delete removes`() {
        val existing = Bond(
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
        whenever(repository.findById(1L)).thenReturn(Optional.of(existing))
        whenever(repository.save(any<Bond>())).thenAnswer { invocation ->
            invocation.getArgument<Bond>(0)
        }

        val updated = service.update(
            1L,
            UpdateBondRequest(
                ticker = "26244",
                isin = "RU000A1038V7",
                name = "ОФЗ-ПД 26244",
                type = "GOVERNMENT",
                nominal = 1000.0,
                couponRate = 15.0,
                maturityDate = LocalDate.of(2040, 1, 1),
                currency = "RUB",
                couponPeriodDays = 182
            )
        )
        assertEquals("26244", updated.ticker)
        assertEquals(15.0, updated.couponRate)

        service.delete(1L)
        org.mockito.kotlin.verify(repository).delete(existing)
    }

    @Test
    fun `update and delete throw when missing`() {
        whenever(repository.findById(99L)).thenReturn(Optional.empty())
        val request = UpdateBondRequest(
            ticker = "x",
            name = "x",
            nominal = 1000.0,
            couponRate = 1.0,
            maturityDate = null,
            currency = "RUB",
            couponPeriodDays = 182
        )
        assertThrows(BondNotFoundException::class.java) { service.update(99L, request) }
        assertThrows(BondNotFoundException::class.java) { service.delete(99L) }
    }
}

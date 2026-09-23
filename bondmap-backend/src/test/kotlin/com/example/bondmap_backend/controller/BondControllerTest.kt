package com.example.bondmap_backend.controller

import com.example.bondmap_backend.dto.BondResponse
import com.example.bondmap_backend.dto.CreateBondRequest
import com.example.bondmap_backend.dto.UpdateBondRequest
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.exception.GlobalExceptionHandler
import com.example.bondmap_backend.service.BondService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(controllers = [BondController::class])
@Import(GlobalExceptionHandler::class)
class BondControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var bondService: BondService

    private val sampleBond = BondResponse(
        id = 1L,
        ticker = "26238",
        isin = "RU000A102345",
        name = "OFZ-PD 26238",
        type = "GOVERNMENT",
        typeLabel = "Государственная облигация",
        nominal = 1000.0,
        couponRate = 15.5,
        couponFrequency = 2,
        maturityDate = LocalDate.of(2039, 7, 15),
        currency = "RUB",
        couponPeriodDays = 182
    )

    @Test
    fun `GET api bonds returns list`() {
        whenever(bondService.getAll()).thenReturn(listOf(sampleBond))

        mockMvc.perform(get("/api/bonds"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].ticker").value("26238"))
            .andExpect(jsonPath("$[0].isin").value("RU000A102345"))
    }

    @Test
    fun `GET api bonds by id returns bond`() {
        whenever(bondService.getById(1L)).thenReturn(sampleBond)

        mockMvc.perform(get("/api/bonds/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("OFZ-PD 26238"))
    }

    @Test
    fun `GET api bonds by id returns 404 when missing`() {
        whenever(bondService.getById(99L)).thenThrow(BondNotFoundException(99L))

        mockMvc.perform(get("/api/bonds/99"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `POST api bonds creates bond`() {
        whenever(bondService.create(any())).thenReturn(sampleBond)

        val body = """
            {
              "ticker": "RU000A102345",
              "name": "OFZ-PD 26238",
              "nominal": 1000.0,
              "couponRate": 15.5,
              "maturityDate": "2039-07-15",
              "currency": "RUB",
              "couponPeriodDays": 182
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/bonds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))

        verify(bondService).create(
            check<CreateBondRequest> {
                assert(it.ticker == "RU000A102345")
                assert(it.name == "OFZ-PD 26238")
                assert(it.nominal == 1000.0)
                assert(it.couponRate == 15.5)
                assert(it.currency == "RUB")
            }
        )
    }

    @Test
    fun `PUT api bonds updates bond`() {
        whenever(bondService.update(any(), any())).thenReturn(sampleBond)

        val body = """
            {
              "ticker": "RU000A102345",
              "name": "OFZ-PD 26238",
              "nominal": 1000.0,
              "couponRate": 15.5,
              "maturityDate": "2039-07-15",
              "currency": "RUB",
              "couponPeriodDays": 182
            }
        """.trimIndent()

        mockMvc.perform(
            put("/api/bonds/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.ticker").value("26238"))
            .andExpect(jsonPath("$.isin").value("RU000A102345"))

        verify(bondService).update(
            eq(1L),
            check<UpdateBondRequest> {
                assert(it.ticker == "RU000A102345")
                assert(it.couponRate == 15.5)
            }
        )
    }

    @Test
    fun `DELETE api bonds returns 204`() {
        mockMvc.perform(delete("/api/bonds/1"))
            .andExpect(status().isNoContent)
        verify(bondService).delete(1L)
    }

    @Test
    fun `POST api bonds rejects blank ticker`() {
        val body = """
            {
              "ticker": "",
              "name": "OFZ",
              "nominal": 1000.0,
              "couponRate": 15.5,
              "currency": "RUB"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/bonds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Validation failed"))
    }
}

package com.example.bondmap_backend.controller

import com.example.bondmap_backend.dto.BondPriceResponse
import com.example.bondmap_backend.dto.CreateBondPriceRequest
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.exception.GlobalExceptionHandler
import com.example.bondmap_backend.service.BondPriceService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(controllers = [BondPriceController::class])
@Import(GlobalExceptionHandler::class)
class BondPriceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var bondPriceService: BondPriceService

    private val sample = BondPriceResponse(
        id = 5L,
        price = 945.8,
        priceDate = LocalDate.of(2026, 9, 15),
        open = 944.0,
        high = 947.0,
        low = 943.0,
        close = 945.8,
        volume = 1_000_000.0,
        source = "SIMULATION"
    )

    @Test
    fun `GET prices returns history`() {
        whenever(bondPriceService.getHistory(1L)).thenReturn(listOf(sample))

        mockMvc.perform(get("/api/bonds/1/prices"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].close").value(945.8))
            .andExpect(jsonPath("$[0].source").value("SIMULATION"))
        verify(bondPriceService).getHistory(1L)
    }

    @Test
    fun `GET prices returns 404 when bond missing`() {
        whenever(bondPriceService.getHistory(99L)).thenThrow(BondNotFoundException(99L))

        mockMvc.perform(get("/api/bonds/99/prices"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `POST prices stores a quote`() {
        whenever(bondPriceService.create(any(), any())).thenReturn(sample)

        val body = """
            {
              "price": 945.8,
              "priceDate": "2026-09-15",
              "open": 944.0,
              "high": 947.0,
              "low": 943.0,
              "close": 945.8
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/bonds/1/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(5))

        verify(bondPriceService).create(
            eq(1L),
            check<CreateBondPriceRequest> {
                assert(it.price == 945.8)
                assert(it.priceDate == LocalDate.of(2026, 9, 15))
                assert(it.open == 944.0)
                assert(it.high == 947.0)
                assert(it.low == 943.0)
                assert(it.close == 945.8)
            }
        )
    }
}

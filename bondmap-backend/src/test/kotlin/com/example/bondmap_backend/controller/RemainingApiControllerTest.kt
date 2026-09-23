package com.example.bondmap_backend.controller

import com.example.bondmap_backend.dto.BondAnalyticsResponse
import com.example.bondmap_backend.dto.BondDetailsResponse
import com.example.bondmap_backend.dto.BondSearchResponse
import com.example.bondmap_backend.dto.BondSummaryResponse
import com.example.bondmap_backend.dto.CreateKeyRateRequest
import com.example.bondmap_backend.dto.KeyRateResponse
import com.example.bondmap_backend.dto.MarketDataResponse
import com.example.bondmap_backend.dto.ScenarioResponse
import com.example.bondmap_backend.dto.YieldResponse
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.exception.GlobalExceptionHandler
import com.example.bondmap_backend.service.BondAnalyticsService
import com.example.bondmap_backend.service.BondDetailsService
import com.example.bondmap_backend.service.BondSearchService
import com.example.bondmap_backend.service.BondSummaryService
import com.example.bondmap_backend.service.KeyRateService
import com.example.bondmap_backend.service.YieldService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
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

@WebMvcTest(
    controllers = [
        BondDetailsController::class,
        BondAnalyticsController::class,
        BondSummaryController::class,
        YieldController::class,
        BondSearchController::class,
        KeyRateController::class
    ]
)
@Import(GlobalExceptionHandler::class)
class RemainingApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var detailsService: BondDetailsService

    @MockitoBean
    private lateinit var analyticsService: BondAnalyticsService

    @MockitoBean
    private lateinit var summaryService: BondSummaryService

    @MockitoBean
    private lateinit var yieldService: YieldService

    @MockitoBean
    private lateinit var searchService: BondSearchService

    @MockitoBean
    private lateinit var keyRateService: KeyRateService

    @Test
    fun `routes pass path id to services`() {
        whenever(detailsService.getDetails(1L)).thenReturn(details())
        whenever(analyticsService.getAnalytics(1L)).thenReturn(analytics())
        whenever(analyticsService.runScenario(1L, 50)).thenReturn(scenario())
        whenever(yieldService.calculateYield(1L)).thenReturn(
            YieldResponse("26243", "RU000A1038V6", 1000.0, 945.8, 14.0, 140.0, 14.8, 14.9)
        )
        whenever(summaryService.getAll()).thenReturn(
            listOf(
                BondSummaryResponse(
                    1L, "26243", "RU000A1038V6", "ОФЗ-ПД 26243", "RUB",
                    "GOVERNMENT", 945.8, 14.0, 14.9, 14.8, LocalDate.of(2038, 5, 19)
                )
            )
        )

        mockMvc.perform(get("/api/bonds/1/details")).andExpect(status().isOk)
        mockMvc.perform(get("/api/bonds/1/analytics")).andExpect(status().isOk)
        mockMvc.perform(get("/api/bonds/1/scenario").param("shockBp", "50")).andExpect(status().isOk)
        mockMvc.perform(get("/api/bonds/1/yield")).andExpect(status().isOk)
        mockMvc.perform(get("/api/bonds/summary")).andExpect(status().isOk)

        verify(detailsService).getDetails(1L)
        verify(analyticsService).getAnalytics(1L)
        verify(analyticsService).runScenario(1L, 50)
        verify(yieldService).calculateYield(1L)
        verify(summaryService).getAll()
    }

    @Test
    fun `details missing is 404`() {
        whenever(detailsService.getDetails(99L)).thenThrow(BondNotFoundException(99L))
        mockMvc.perform(get("/api/bonds/99/details")).andExpect(status().isNotFound)
    }

    @Test
    fun `search forwards currency and yield filters`() {
        whenever(
            searchService.search(isNull(), eq("RUB"), isNull(), isNull(), isNull(), isNull(), eq(10.0), eq(20.0), eq("yield_desc"))
        ).thenReturn(
            listOf(
                BondSearchResponse(
                    1L, "26243", "RU000A1038V6", "ОФЗ-ПД 26243", "RUB",
                    14.0, 945.8, 14.9, 14.8, LocalDate.of(2038, 5, 19)
                )
            )
        )
        mockMvc.perform(
            get("/api/bonds/search")
                .param("currency", "RUB")
                .param("minYield", "10")
                .param("maxYield", "20")
                .param("sort", "yield_desc")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].isin").value("RU000A1038V6"))

        verify(searchService).search(
            isNull(), eq("RUB"), isNull(), isNull(), isNull(), isNull(),
            eq(10.0), eq(20.0), eq("yield_desc")
        )
    }

    @Test
    fun `key rate create maps json body and returns 201`() {
        whenever(keyRateService.create(org.mockito.kotlin.any())).thenReturn(
            KeyRateResponse(4L, 15.0, LocalDate.of(2026, 9, 22))
        )

        mockMvc.perform(
            post("/api/key-rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"rate":15.0,"rateDate":"2026-09-22"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(4))

        verify(keyRateService).create(
            check<CreateKeyRateRequest> {
                assert(it.rate == 15.0)
                assert(it.rateDate == LocalDate.of(2026, 9, 22))
            }
        )
    }

    private fun details() = BondDetailsResponse(
        id = 1L,
        ticker = "26243",
        isin = "RU000A1038V6",
        name = "ОФЗ-ПД 26243",
        type = "GOVERNMENT",
        typeLabel = "Государственная облигация",
        currency = "RUB",
        nominal = 1000.0,
        currentPrice = 945.8,
        couponRate = 14.0,
        couponFrequency = 2,
        couponPeriodDays = 182,
        maturityDate = LocalDate.of(2038, 5, 19),
        annualCouponIncome = 140.0,
        currentYield = 14.8,
        ytm = 14.9,
        marketData = MarketDataResponse(945.8, 1.0, 14.9, LocalDate.of(2026, 9, 15)),
        priceHistory = emptyList()
    )

    private fun analytics() = BondAnalyticsResponse(
        ticker = "26243",
        isin = "RU000A1038V6",
        currentPrice = 945.8,
        ytm = 14.9,
        currentYield = 14.8,
        baseRate = 14.0,
        baseRateDate = LocalDate.of(2026, 9, 15),
        spreadToBaseRate = 0.9,
        modifiedDuration = 5.3,
        macaulayDuration = 5.5,
        convexity = 40.0,
        keyRate = 14.0,
        keyRateDate = LocalDate.of(2026, 9, 15),
        spreadToKeyRate = 0.9,
        approxDurationYears = 5.3
    )

    private fun scenario() = ScenarioResponse(
        ticker = "26243",
        shockBp = 50,
        currentPrice = 945.8,
        estimatedNewPrice = 920.0,
        estimatedPriceChange = -25.8,
        estimatedPriceChangePercent = -2.7,
        modifiedDuration = 5.3,
        deltaY = 0.005,
        formula = "ΔP ≈ -D_mod × Δy × P",
        note = "note",
        approxDurationYears = 5.3
    )
}

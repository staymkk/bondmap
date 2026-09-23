package com.example.bondmap.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BondApi {
    @GET("api/bonds")
    suspend fun getBonds(): List<BondDto>

    @GET("api/bonds/summary")
    suspend fun getBondSummaries(): List<BondSummaryDto>

    @GET("api/bonds/{id}/details")
    suspend fun getBondDetails(@Path("id") id: Long): BondDetailsDto

    @GET("api/bonds/search")
    suspend fun searchBonds(
        @Query("query") query: String? = null,
        @Query("currency") currency: String? = null,
        @Query("isin") isin: String? = null,
        @Query("name") name: String? = null,
        @Query("maturityFrom") maturityFrom: String? = null,
        @Query("maturityTo") maturityTo: String? = null,
        @Query("minYield") minYield: Double? = null,
        @Query("maxYield") maxYield: Double? = null,
        @Query("sort") sort: String? = null
    ): List<BondSearchDto>

    @GET("api/bonds/{id}/analytics")
    suspend fun getAnalytics(@Path("id") id: Long): BondAnalyticsDto

    @GET("api/bonds/{id}/scenario")
    suspend fun getScenario(
        @Path("id") id: Long,
        @Query("shockBp") shockBp: Int
    ): ScenarioDto
}

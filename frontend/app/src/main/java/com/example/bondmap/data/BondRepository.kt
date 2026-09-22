package com.example.bondmap.data

class BondRepository(
    private val api: BondApi = NetworkModule.api
) {
    suspend fun getBonds(): List<BondDto> = api.getBonds()

    suspend fun getBondSummaries(): List<BondSummaryDto> = api.getBondSummaries()

    suspend fun getDetails(id: Long): BondDetailsDto = api.getBondDetails(id)

    suspend fun search(
        currency: String?,
        minYield: Double?,
        maxYield: Double?,
        sort: String?
    ): List<BondSearchDto> = api.searchBonds(currency, minYield, maxYield, sort)

    suspend fun getAnalytics(id: Long): BondAnalyticsDto = api.getAnalytics(id)

    suspend fun getScenario(id: Long, shockBp: Int): ScenarioDto =
        api.getScenario(id, shockBp)
}

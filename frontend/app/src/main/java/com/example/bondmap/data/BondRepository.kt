package com.example.bondmap.data

class BondRepository(
    private val api: BondApi = NetworkModule.api
) {
    suspend fun getBonds(): List<BondDto> = api.getBonds()

    suspend fun getBondSummaries(): List<BondSummaryDto> = api.getBondSummaries()

    suspend fun getDetails(id: Long): BondDetailsDto = api.getBondDetails(id)

    suspend fun search(
        query: String? = null,
        currency: String? = null,
        isin: String? = null,
        name: String? = null,
        maturityFrom: String? = null,
        maturityTo: String? = null,
        minYield: Double? = null,
        maxYield: Double? = null,
        sort: String? = null
    ): List<BondSearchDto> = api.searchBonds(
        query = query,
        currency = currency,
        isin = isin,
        name = name,
        maturityFrom = maturityFrom,
        maturityTo = maturityTo,
        minYield = minYield,
        maxYield = maxYield,
        sort = sort
    )

    suspend fun getAnalytics(id: Long): BondAnalyticsDto = api.getAnalytics(id)

    suspend fun getScenario(id: Long, shockBp: Int): ScenarioDto =
        api.getScenario(id, shockBp)
}

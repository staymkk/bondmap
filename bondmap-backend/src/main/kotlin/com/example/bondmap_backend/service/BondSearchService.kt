package com.example.bondmap_backend.service

import com.example.bondmap_backend.dto.BondSearchResponse
import com.example.bondmap_backend.repository.BondRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BondSearchService(
    private val bondRepository: BondRepository,
    private val valuationService: BondValuationService
) {

    fun search(
        query: String?,
        currency: String?,
        isin: String?,
        name: String?,
        maturityFrom: LocalDate?,
        maturityTo: LocalDate?,
        minYield: Double?,
        maxYield: Double?,
        sort: String?
    ): List<BondSearchResponse> {
        val q = query?.trim()?.lowercase()?.takeIf { it.isNotEmpty() }
        val isinFilter = isin?.trim()?.lowercase()?.takeIf { it.isNotEmpty() }
        val nameFilter = name?.trim()?.lowercase()?.takeIf { it.isNotEmpty() }
        val currencyFilter = currency?.trim()?.takeIf { it.isNotEmpty() && !it.equals("ALL", ignoreCase = true) }

        return bondRepository.findAll()
            .map { valuationService.toSearch(it) }
            .filter { bond ->
                q == null ||
                    bond.name.lowercase().contains(q) ||
                    bond.isin.lowercase().contains(q) ||
                    bond.ticker.lowercase().contains(q)
            }
            .filter { bond -> currencyFilter == null || bond.currency.equals(currencyFilter, ignoreCase = true) }
            .filter { bond -> isinFilter == null || bond.isin.lowercase().contains(isinFilter) }
            .filter { bond -> nameFilter == null || bond.name.lowercase().contains(nameFilter) }
            .filter { bond ->
                maturityFrom == null || (bond.maturityDate != null && !bond.maturityDate.isBefore(maturityFrom))
            }
            .filter { bond ->
                maturityTo == null || (bond.maturityDate != null && !bond.maturityDate.isAfter(maturityTo))
            }
            .filter { bond ->
                minYield == null || (bond.ytm ?: bond.currentYield ?: 0.0) >= minYield
            }
            .filter { bond ->
                maxYield == null || (bond.ytm ?: bond.currentYield ?: 0.0) <= maxYield
            }
            .sortedWith(
                when (sort) {
                    "yield_desc" -> compareByDescending { it.ytm ?: it.currentYield ?: 0.0 }
                    "yield_asc" -> compareBy { it.ytm ?: it.currentYield ?: 0.0 }
                    else -> compareBy { it.name }
                }
            )
    }
}

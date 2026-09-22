package com.example.bondmap_backend.service

import com.example.bondmap_backend.dto.BondSearchResponse
import com.example.bondmap_backend.repository.BondPriceRepository
import com.example.bondmap_backend.repository.BondRepository
import org.springframework.stereotype.Service

@Service
class BondSearchService(
    private val bondRepository: BondRepository,
    private val bondPriceRepository: BondPriceRepository
) {

    fun search(
        currency: String?,
        minYield: Double?,
        maxYield: Double?,
        sort: String?
    ): List<BondSearchResponse> {

        return bondRepository.findAll()
            .map { bond ->

                val price = bondPriceRepository
                    .findTopByBondIdOrderByPriceDateDesc(bond.id!!)
                    ?.price

                val annualCouponIncome =
                    bond.nominal * bond.couponRate / 100

                val currentYield = price?.let {
                    annualCouponIncome / it * 100
                }

                BondSearchResponse(
                    id = bond.id!!,
                    ticker = bond.ticker,
                    name = bond.name,
                    currency = bond.currency,
                    currentYield = currentYield
                )
            }
            .filter { bond ->
                currency == null || bond.currency == currency
            }
            .filter { bond ->
                minYield == null ||
                        (bond.currentYield ?: 0.0) >= minYield
            }
            .filter { bond ->
                maxYield == null ||
                        (bond.currentYield ?: 0.0) <= maxYield
            }
            .sortedWith(
                when(sort) {
                    "yield_desc" ->
                        compareByDescending<BondSearchResponse> {
                            it.currentYield ?: 0.0
                        }

                    "yield_asc" ->
                        compareBy {
                            it.currentYield ?: 0.0
                        }

                    else ->
                        compareBy { it.ticker }
                }
            )
    }
}
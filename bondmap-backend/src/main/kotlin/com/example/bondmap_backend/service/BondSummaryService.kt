package com.example.bondmap_backend.service

import com.example.bondmap_backend.dto.BondSummaryResponse
import com.example.bondmap_backend.repository.BondPriceRepository
import com.example.bondmap_backend.repository.BondRepository
import org.springframework.stereotype.Service

@Service
class BondSummaryService(
    private val bondRepository: BondRepository,
    private val bondPriceRepository: BondPriceRepository
) {

    fun getAll(): List<BondSummaryResponse> {

        return bondRepository.findAll()
            .map { bond ->

                val bondId = bond.id
                    ?: throw IllegalStateException("Bond id is null")


                val annualCouponIncome =
                    bond.nominal * bond.couponRate / 100


                val currentPrice =
                    bondPriceRepository
                        .findTopByBondIdOrderByPriceDateDesc(bondId)
                        ?.price


                val currentYield =
                    currentPrice?.let { price ->
                        annualCouponIncome / price * 100
                    }


                BondSummaryResponse(
                    id = bondId,
                    ticker = bond.ticker,
                    name = bond.name,
                    currency = bond.currency,
                    currentPrice = currentPrice,
                    currentYield = currentYield,
                    maturityDate = bond.maturityDate
                )
            }
    }
}
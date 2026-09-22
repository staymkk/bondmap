package com.example.bondmap_backend.service

import com.example.bondmap_backend.dto.BondDetailsResponse
import com.example.bondmap_backend.dto.PricePoint
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.repository.BondPriceRepository
import com.example.bondmap_backend.repository.BondRepository
import org.springframework.stereotype.Service

@Service
class BondDetailsService(
    private val bondRepository: BondRepository,
    private val bondPriceRepository: BondPriceRepository
) {

    fun getDetails(id: Long): BondDetailsResponse {

        val bond = bondRepository.findById(id)
            .orElseThrow { BondNotFoundException(id) }


        val annualCouponIncome =
            bond.nominal * bond.couponRate / 100


        val latestPrice =
            bondPriceRepository.findTopByBondIdOrderByPriceDateDesc(id)
                ?.price


        val currentYield = latestPrice?.let { price ->
            annualCouponIncome / price * 100
        }


        val history = bondPriceRepository.findAllByBondId(id)
            .map {
                PricePoint(
                    price = it.price,
                    date = it.priceDate
                )
            }


        return BondDetailsResponse(
            ticker = bond.ticker,
            name = bond.name,
            currency = bond.currency,
            nominal = bond.nominal,
            currentPrice = latestPrice,
            couponRate = bond.couponRate,
            annualCouponIncome = annualCouponIncome,
            currentYield = currentYield,
            priceHistory = history
        )
    }
}
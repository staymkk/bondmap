package com.example.bondmap_backend.service

import com.example.bondmap_backend.dto.YieldResponse
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.repository.BondRepository
import com.example.bondmap_backend.repository.BondPriceRepository
import org.springframework.stereotype.Service

@Service
class YieldService(
    private val bondRepository: BondRepository,
    private val bondPriceRepository: BondPriceRepository
) {

    fun calculateYield(id: Long): YieldResponse {

        val bond = bondRepository.findById(id)
            .orElseThrow { BondNotFoundException(id) }


        val annualCouponIncome =
            bond.nominal * bond.couponRate / 100


        val currentPrice =
            bondPriceRepository
                .findTopByBondIdOrderByPriceDateDesc(id)
                ?.price


        val currentYield =
            currentPrice?.let { price ->
                annualCouponIncome / price * 100
            }


        return YieldResponse(
            ticker = bond.ticker,
            nominal = bond.nominal,
            currentPrice = currentPrice,
            couponRate = bond.couponRate,
            annualCouponIncome = annualCouponIncome,
            currentYield = currentYield
        )
    }
}
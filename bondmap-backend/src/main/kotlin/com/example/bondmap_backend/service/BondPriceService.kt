package com.example.bondmap_backend.service

import com.example.bondmap_backend.domain.BondPrice
import com.example.bondmap_backend.dto.BondPriceResponse
import com.example.bondmap_backend.dto.CreateBondPriceRequest
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.repository.BondPriceRepository
import com.example.bondmap_backend.repository.BondRepository
import org.springframework.stereotype.Service

@Service
class BondPriceService(
    private val bondRepository: BondRepository,
    private val bondPriceRepository: BondPriceRepository
) {

    fun create(
        bondId: Long,
        request: CreateBondPriceRequest
    ): BondPriceResponse {

        val bond = bondRepository.findById(bondId)
            .orElseThrow { BondNotFoundException(bondId) }

        val bondPrice = BondPrice(
            bond = bond,
            price = request.price,
            priceDate = request.priceDate
        )

        return toResponse(
            bondPriceRepository.save(bondPrice)
        )
    }


    private fun toResponse(
        bondPrice: BondPrice
    ): BondPriceResponse {

        return BondPriceResponse(
            id = bondPrice.id!!,
            price = bondPrice.price,
            priceDate = bondPrice.priceDate
        )
    }
    fun getHistory(bondId: Long): List<BondPriceResponse> {

        if (!bondRepository.existsById(bondId)) {
            throw BondNotFoundException(bondId)
        }

        return bondPriceRepository.findAllByBondId(bondId)
            .map { toResponse(it) }
    }
}
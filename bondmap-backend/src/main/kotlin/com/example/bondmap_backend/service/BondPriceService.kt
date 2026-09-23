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

        val close = request.close ?: request.price
        val open = request.open ?: close
        val high = request.high ?: maxOf(open, close)
        val low = request.low ?: minOf(open, close)

        val bondPrice = BondPrice(
            bond = bond,
            price = close,
            priceDate = request.priceDate,
            openPrice = open,
            highPrice = high,
            lowPrice = low,
            closePrice = close,
            volume = request.volume,
            source = BondCalculator.SIMULATION_SOURCE
        )

        return toResponse(bondPriceRepository.save(bondPrice))
    }

    fun getHistory(bondId: Long): List<BondPriceResponse> {
        if (!bondRepository.existsById(bondId)) {
            throw BondNotFoundException(bondId)
        }

        return bondPriceRepository.findAllByBondIdOrderByPriceDateAsc(bondId)
            .map { toResponse(it) }
    }

    private fun toResponse(bondPrice: BondPrice): BondPriceResponse {
        val close = bondPrice.close()
        return BondPriceResponse(
            id = bondPrice.id!!,
            price = close,
            priceDate = bondPrice.priceDate,
            open = bondPrice.open(),
            high = bondPrice.high(),
            low = bondPrice.low(),
            close = close,
            volume = bondPrice.volume,
            source = bondPrice.source.ifBlank { BondCalculator.SIMULATION_SOURCE }
        )
    }
}

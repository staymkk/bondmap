package com.example.bondmap_backend.service

import com.example.bondmap_backend.dto.BondDetailsResponse
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.repository.BondPriceRepository
import com.example.bondmap_backend.repository.BondRepository
import org.springframework.stereotype.Service

@Service
class BondDetailsService(
    private val bondRepository: BondRepository,
    private val bondPriceRepository: BondPriceRepository,
    private val valuationService: BondValuationService
) {

    fun getDetails(id: Long): BondDetailsResponse {
        val bond = bondRepository.findById(id)
            .orElseThrow { BondNotFoundException(id) }
        val history = bondPriceRepository.findAllByBondIdOrderByPriceDateAsc(id)
        return valuationService.toDetails(bond, history)
    }
}

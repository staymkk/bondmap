package com.example.bondmap_backend.service

import com.example.bondmap_backend.dto.YieldResponse
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.repository.BondRepository
import org.springframework.stereotype.Service

@Service
class YieldService(
    private val bondRepository: BondRepository,
    private val valuationService: BondValuationService
) {

    fun calculateYield(id: Long): YieldResponse {
        val bond = bondRepository.findById(id)
            .orElseThrow { BondNotFoundException(id) }
        return valuationService.toYield(bond)
    }
}

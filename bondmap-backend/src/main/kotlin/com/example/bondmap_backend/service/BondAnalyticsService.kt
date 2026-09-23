package com.example.bondmap_backend.service

import com.example.bondmap_backend.dto.BondAnalyticsResponse
import com.example.bondmap_backend.dto.ScenarioResponse
import com.example.bondmap_backend.exception.BondNotFoundException
import com.example.bondmap_backend.repository.BondRepository
import org.springframework.stereotype.Service

@Service
class BondAnalyticsService(
    private val bondRepository: BondRepository,
    private val valuationService: BondValuationService
) {

    fun getAnalytics(bondId: Long): BondAnalyticsResponse {
        val bond = bondRepository.findById(bondId)
            .orElseThrow { BondNotFoundException(bondId) }
        return valuationService.toAnalytics(bond)
    }

    fun runScenario(bondId: Long, shockBp: Int): ScenarioResponse {
        val bond = bondRepository.findById(bondId)
            .orElseThrow { BondNotFoundException(bondId) }
        return valuationService.toScenario(bond, shockBp)
    }
}

package com.example.bondmap_backend.service

import com.example.bondmap_backend.dto.BondSummaryResponse
import com.example.bondmap_backend.repository.BondRepository
import org.springframework.stereotype.Service

@Service
class BondSummaryService(
    private val bondRepository: BondRepository,
    private val valuationService: BondValuationService
) {

    fun getAll(): List<BondSummaryResponse> {
        return bondRepository.findAll().map { valuationService.toSummary(it) }
    }
}

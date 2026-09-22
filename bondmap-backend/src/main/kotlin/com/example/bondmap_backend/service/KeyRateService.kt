package com.example.bondmap_backend.service

import com.example.bondmap_backend.domain.KeyRate
import com.example.bondmap_backend.dto.CreateKeyRateRequest
import com.example.bondmap_backend.dto.KeyRateResponse
import com.example.bondmap_backend.repository.KeyRateRepository
import org.springframework.stereotype.Service

@Service
class KeyRateService(
    private val keyRateRepository: KeyRateRepository
) {

    fun getLatest(): KeyRateResponse? =
        keyRateRepository.findTopByOrderByRateDateDesc()?.toResponse()

    fun getHistory(): List<KeyRateResponse> =
        keyRateRepository.findAllByOrderByRateDateAsc().map { it.toResponse() }

    fun create(request: CreateKeyRateRequest): KeyRateResponse {
        val saved = keyRateRepository.save(
            KeyRate(rate = request.rate, rateDate = request.rateDate)
        )
        return saved.toResponse()
    }

    private fun KeyRate.toResponse() = KeyRateResponse(
        id = id!!,
        rate = rate,
        rateDate = rateDate
    )
}

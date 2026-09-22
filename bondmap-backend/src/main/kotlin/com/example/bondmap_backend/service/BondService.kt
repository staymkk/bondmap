package com.example.bondmap_backend.service

import com.example.bondmap_backend.domain.Bond
import com.example.bondmap_backend.repository.BondRepository
import org.springframework.stereotype.Service
import com.example.bondmap_backend.dto.CreateBondRequest
import com.example.bondmap_backend.dto.BondResponse
import com.example.bondmap_backend.dto.UpdateBondRequest
import com.example.bondmap_backend.exception.BondNotFoundException

@Service
class BondService(
    private val bondRepository: BondRepository
) {

    fun getAll(): List<BondResponse> {
        return bondRepository.findAll().map { toResponse(it) }
    }

    fun getById(id: Long): BondResponse {
        return toResponse(
            bondRepository.findById(id)
                .orElseThrow { BondNotFoundException(id) }
        )
    }

    fun create(request: CreateBondRequest): BondResponse {
        val bond = Bond(
            ticker = request.ticker,
            name = request.name,
            nominal = request.nominal,
            couponRate = request.couponRate,
            maturityDate = request.maturityDate,
            currency = request.currency,
            couponPeriodDays = request.couponPeriodDays,
        )

        return toResponse(bondRepository.save(bond))
    }

    fun update(id: Long, request: UpdateBondRequest): BondResponse {
        val existingBond = bondRepository.findById(id)
            .orElseThrow { BondNotFoundException(id) }

        val updatedBond = Bond(
            id = existingBond.id,
            ticker = request.ticker,
            name = request.name,
            nominal = request.nominal,
            couponRate = request.couponRate,
            maturityDate = request.maturityDate,
            currency = request.currency,
            couponPeriodDays = request.couponPeriodDays
        )

        return toResponse(bondRepository.save(updatedBond))
    }

    fun delete(id: Long) {
        val bond = bondRepository.findById(id)
            .orElseThrow { BondNotFoundException(id) }

        bondRepository.delete(bond)
    }

    fun toResponse(bond: Bond): BondResponse {
        return BondResponse(
            id = bond.id!!,
            ticker = bond.ticker,
            name = bond.name,
            nominal = bond.nominal,
            couponRate = bond.couponRate,
            maturityDate = bond.maturityDate,
            currency = bond.currency,
            couponPeriodDays = bond.couponPeriodDays
        )
    }
}
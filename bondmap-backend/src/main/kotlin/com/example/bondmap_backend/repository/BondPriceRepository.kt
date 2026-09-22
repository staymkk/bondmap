package com.example.bondmap_backend.repository

import com.example.bondmap_backend.domain.BondPrice
import org.springframework.data.jpa.repository.JpaRepository

interface BondPriceRepository :
    JpaRepository<BondPrice, Long> {

    fun findAllByBondId(bondId: Long): List<BondPrice>

    fun findTopByBondIdOrderByPriceDateDesc(bondId: Long): BondPrice?
}
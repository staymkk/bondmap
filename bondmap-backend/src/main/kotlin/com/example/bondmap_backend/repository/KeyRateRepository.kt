package com.example.bondmap_backend.repository

import com.example.bondmap_backend.domain.KeyRate
import org.springframework.data.jpa.repository.JpaRepository

interface KeyRateRepository : JpaRepository<KeyRate, Long> {
    fun findTopByOrderByRateDateDesc(): KeyRate?
    fun findAllByOrderByRateDateAsc(): List<KeyRate>
}

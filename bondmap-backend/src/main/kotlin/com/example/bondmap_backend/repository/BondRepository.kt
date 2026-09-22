package com.example.bondmap_backend.repository

import com.example.bondmap_backend.domain.Bond
import org.springframework.data.jpa.repository.JpaRepository

interface BondRepository : JpaRepository<Bond, Long>
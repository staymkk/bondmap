package com.example.bondmap_backend.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "key_rates")
class KeyRate(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val rate: Double,

    @Column(name = "rate_date")
    val rateDate: LocalDate
)

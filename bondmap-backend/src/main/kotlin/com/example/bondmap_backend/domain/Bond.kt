package com.example.bondmap_backend.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "bonds")
class Bond(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val ticker: String,

    val name: String,

    val nominal: Double,

    val couponRate: Double,

    @Column(name = "maturity_date")
    val maturityDate: LocalDate? = null,

    val currency: String = "RUB",

    @Column(name = "coupon_period_days")
    val couponPeriodDays: Int? = null
)
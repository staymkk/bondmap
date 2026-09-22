package com.example.bondmap_backend.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "bond_prices")
class BondPrice(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bond_id")
    val bond: Bond,

    val price: Double,

    @Column(name = "price_date")
    val priceDate: LocalDate
)
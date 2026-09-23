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
    val priceDate: LocalDate,

    @Column(name = "open_price")
    val openPrice: Double? = null,

    @Column(name = "high_price")
    val highPrice: Double? = null,

    @Column(name = "low_price")
    val lowPrice: Double? = null,

    @Column(name = "close_price")
    val closePrice: Double? = null,

    val volume: Double? = null,

    val source: String = "SIMULATION"
) {
    fun close(): Double = closePrice ?: price
    fun open(): Double = openPrice ?: close()
    fun high(): Double = highPrice ?: maxOf(open(), close())
    fun low(): Double = lowPrice ?: minOf(open(), close())
}

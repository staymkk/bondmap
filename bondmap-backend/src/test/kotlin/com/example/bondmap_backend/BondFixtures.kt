package com.example.bondmap_backend

import com.example.bondmap_backend.domain.Bond
import com.example.bondmap_backend.domain.BondPrice
import com.example.bondmap_backend.domain.KeyRate
import java.time.LocalDate

object BondFixtures {

    fun bond(
        id: Long? = 1L,
        ticker: String = "26243",
        name: String = "ОФЗ-ПД 26243",
        isin: String = "RU000A1038V6",
        currency: String = "RUB",
        couponRate: Double = 14.0,
        couponPeriodDays: Int? = 182,
        maturity: LocalDate? = LocalDate.of(2038, 5, 19),
        type: String = "GOVERNMENT"
    ) = Bond(
        id = id,
        ticker = ticker,
        name = name,
        nominal = 1000.0,
        couponRate = couponRate,
        maturityDate = maturity,
        currency = currency,
        couponPeriodDays = couponPeriodDays,
        isin = isin,
        bondType = type
    )

    fun price(
        bond: Bond = bond(),
        id: Long? = 5L,
        close: Double = 945.8,
        date: LocalDate = LocalDate.of(2026, 9, 15),
        open: Double? = 944.0,
        high: Double? = 947.0,
        low: Double? = 943.0,
        volume: Double? = 1_000_000.0,
        source: String = "SIMULATION"
    ) = BondPrice(
        id = id,
        bond = bond,
        price = close,
        priceDate = date,
        openPrice = open,
        highPrice = high,
        lowPrice = low,
        closePrice = close,
        volume = volume,
        source = source
    )

    fun keyRate(
        id: Long? = 3L,
        rate: Double = 14.0,
        date: LocalDate = LocalDate.of(2026, 9, 15)
    ) = KeyRate(id = id, rate = rate, rateDate = date)
}

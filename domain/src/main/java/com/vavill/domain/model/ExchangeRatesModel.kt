package com.vavill.domain.model

data class ExchangeRatesModel(
    val currencyName: String,
    val currencyValue: Double,
    val currencyFullName: String,
    var isFavourite: Boolean,
)
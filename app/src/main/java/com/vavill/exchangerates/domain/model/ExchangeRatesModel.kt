package com.vavill.exchangerates.domain.model

import android.graphics.Bitmap

data class ExchangeRatesModel(
    val currencyName: String,
    val currencyValue: Double,
    val currencyFullName: String,
    var isFavourite: Boolean,
    val image: Bitmap?
)
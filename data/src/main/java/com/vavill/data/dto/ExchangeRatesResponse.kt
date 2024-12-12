package com.vavill.data.dto

data class ExchangeRatesResponse(
    val success: Boolean,
    val timestamp: Long,
    val base: String,
    val date: String,
    val rates: Map<String, Double>,
)

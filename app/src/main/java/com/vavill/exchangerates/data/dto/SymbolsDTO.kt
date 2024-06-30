package com.vavill.exchangerates.data.dto

data class SymbolsDTO(
    val success: Boolean,
    val symbols: Map<String, String>
)
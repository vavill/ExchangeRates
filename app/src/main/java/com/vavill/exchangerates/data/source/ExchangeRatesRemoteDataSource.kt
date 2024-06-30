package com.vavill.exchangerates.data.source

import com.vavill.exchangerates.data.api.ExchangeRatesApi
import com.vavill.exchangerates.data.dto.ExchangeRatesResponse
import com.vavill.exchangerates.data.dto.SymbolsDTO
import javax.inject.Inject

interface ExchangeRatesRemoteDataSource {
    suspend fun getExchangeRatesFromApi(baseCurrency: String): ExchangeRatesResponse
    suspend fun getCurrenciesFullNamesFromApi(): SymbolsDTO
}

class ExchangeRatesRemoteDataSourceImpl @Inject constructor(
    private val api: ExchangeRatesApi
): ExchangeRatesRemoteDataSource {

    override suspend fun getExchangeRatesFromApi(baseCurrency: String): ExchangeRatesResponse {
        return api.getExchangeRates(baseCurrency = baseCurrency)
    }

    override suspend fun getCurrenciesFullNamesFromApi(): SymbolsDTO {
        return api.getCurrenciesFullNames()
    }
}
package com.vavill.data.source

import com.vavill.data.api.ExchangeRatesApi
import com.vavill.data.dto.ExchangeRatesResponse
import com.vavill.data.dto.SymbolsDTO
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
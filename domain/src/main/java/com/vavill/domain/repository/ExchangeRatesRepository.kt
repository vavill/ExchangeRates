package com.vavill.domain.repository

import com.vavill.domain.model.ExchangeRatesModel
import kotlinx.coroutines.flow.Flow

interface ExchangeRatesRepository {
    suspend fun getExchangeRatesFromRemoteDataSource(baseCurrency: String): Flow<List<ExchangeRatesModel>>
    suspend fun getCurrenciesFullNamesFromRemoteDataSource(): Map<String, String>
    suspend fun insertCurrencyIntoDb(entity: ExchangeRatesModel)
    suspend fun deleteCurrencyFromDbDataSource(currencyName: String)
    suspend fun getFavouriteCurrenciesFromDbDataSource(): List<ExchangeRatesModel>
    suspend fun update(model: ExchangeRatesModel)
}
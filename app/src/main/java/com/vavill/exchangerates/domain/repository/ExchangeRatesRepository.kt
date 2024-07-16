package com.vavill.exchangerates.domain.repository

import android.graphics.Bitmap
import com.vavill.exchangerates.data.db.entities.ExchangeRatesEntity
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import kotlinx.coroutines.flow.Flow

interface ExchangeRatesRepository {
    suspend fun getExchangeRatesFromRemoteDataSource(baseCurrency: String?): Flow<List<ExchangeRatesModel>>
    suspend fun getCurrenciesFullNamesFromRemoteDataSource(): Map<String, String>
//    suspend fun getAllCurrenciesFromDbDataSource(): Flow<List<ExchangeRatesEntity>>
    suspend fun insertCurrencyIntoDb(entity: ExchangeRatesModel)
    suspend fun deleteCurrencyFromDbDataSource(currencyName: String)
    suspend fun getFavouriteCurrenciesFromDbDataSource(): List<ExchangeRatesModel>
}
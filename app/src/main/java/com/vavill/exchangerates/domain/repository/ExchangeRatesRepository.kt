package com.vavill.exchangerates.domain.repository

import android.graphics.Bitmap
import com.vavill.exchangerates.domain.model.ExchangeRatesModel

interface ExchangeRatesRepository {
    suspend fun getExchangeRatesFromRemoteDataSource(baseCurrency: String): List<ExchangeRatesModel>
    suspend fun getCurrenciesFullNamesFromRemoteDataSource(): Map<String, String>
    suspend fun getAllCurrenciesFromDbDataSource(): List<ExchangeRatesModel>
    suspend fun insertCurrencyIntoDb(entity: ExchangeRatesModel)
    suspend fun deleteCurrencyFromDbDataSource(currencyName: String)
    suspend fun getFavouriteCurrenciesFromDbDataSource(): List<ExchangeRatesModel>
    suspend fun setCurrencyImage(currencyName: String, bitmap: Bitmap)
}
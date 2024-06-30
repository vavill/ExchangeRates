package com.vavill.exchangerates.domain.interactor

import android.graphics.Bitmap
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import com.vavill.exchangerates.domain.repository.ExchangeRatesRepository
import javax.inject.Inject

interface ExchangeRatesInteractor {
    suspend fun getExchangeRatesFromRepository(baseCurrency: String): List<ExchangeRatesModel>
    suspend fun getAllCurrenciesFromRepository(): List<ExchangeRatesModel>
    suspend fun insertCurrencyIntoRepository(model: ExchangeRatesModel)
    suspend fun deleteCurrencyFromRepository(currencyName: String)
    suspend fun getFavouriteCurrenciesFromRepository(): List<ExchangeRatesModel>
    suspend fun setCurrencyImageIntoRepository(currencyName: String, bitmap: Bitmap)
}

class ExchangeRatesInteractorImpl @Inject constructor(
private val repository: ExchangeRatesRepository
): ExchangeRatesInteractor {

    override suspend fun getExchangeRatesFromRepository(
        baseCurrency: String
    ): List<ExchangeRatesModel> =
        repository.getExchangeRatesFromRemoteDataSource(baseCurrency)

    override suspend fun getAllCurrenciesFromRepository(): List<ExchangeRatesModel> =
        repository.getAllCurrenciesFromDbDataSource()

    override suspend fun insertCurrencyIntoRepository(model: ExchangeRatesModel) =
        repository.insertCurrencyIntoDb(model)

    override suspend fun deleteCurrencyFromRepository(currencyName: String) =
        repository.deleteCurrencyFromDbDataSource(currencyName)

    override suspend fun getFavouriteCurrenciesFromRepository(): List<ExchangeRatesModel> =
        repository.getFavouriteCurrenciesFromDbDataSource()

    override suspend fun setCurrencyImageIntoRepository(currencyName: String, bitmap: Bitmap) =
        repository.setCurrencyImage(currencyName, bitmap)
}
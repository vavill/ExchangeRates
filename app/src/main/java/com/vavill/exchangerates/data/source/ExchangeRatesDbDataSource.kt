package com.vavill.exchangerates.data.source

import android.graphics.Bitmap
import com.vavill.exchangerates.data.db.dao.ExchangeRatesDao
import com.vavill.exchangerates.data.db.entities.ExchangeRatesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ExchangeRatesDbDataSource {
    fun getAllCurrenciesFromDb(): Flow<List<ExchangeRatesEntity>>
    suspend fun insertCurrencyIntoDb(entity: ExchangeRatesEntity)
    suspend fun deleteCurrencyFromDb(currencyName: String)
    suspend fun getFavouriteCurrenciesFromDb(): List<ExchangeRatesEntity>
}

class ExchangeRatesDbDataSourceImpl @Inject constructor(
    private val dao: ExchangeRatesDao
) : ExchangeRatesDbDataSource {

    override fun getAllCurrenciesFromDb(): Flow<List<ExchangeRatesEntity>> =
        dao.getAllCurrencies()

    override suspend fun insertCurrencyIntoDb(entity: ExchangeRatesEntity) =
        dao.insertCurrency(entity)

    override suspend fun deleteCurrencyFromDb(currencyName: String) =
        dao.deleteCurrency(currencyName)

    override suspend fun getFavouriteCurrenciesFromDb(): List<ExchangeRatesEntity> =
        dao.getFavouriteCurrencies()
}
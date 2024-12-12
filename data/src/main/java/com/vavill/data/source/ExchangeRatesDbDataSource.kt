package com.vavill.data.source

import com.vavill.data.db.dao.ExchangeRatesDao
import com.vavill.data.db.entities.ExchangeRatesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ExchangeRatesDbDataSource {
    fun getAllCurrenciesFromDb(): Flow<List<ExchangeRatesEntity>>
    suspend fun insert(currency: ExchangeRatesEntity)
    suspend fun insertAll(currencies: List<ExchangeRatesEntity>)
    suspend fun update(currency: ExchangeRatesEntity)
    suspend fun delete(currencyName: String)
    suspend fun getFavourites(): List<ExchangeRatesEntity>
}

class ExchangeRatesDbDataSourceImpl @Inject constructor(
    private val dao: ExchangeRatesDao
) : ExchangeRatesDbDataSource {

    override fun getAllCurrenciesFromDb(): Flow<List<ExchangeRatesEntity>> =
        dao.getAllCurrencies()

    override suspend fun insert(currency: ExchangeRatesEntity) =
        dao.insert(currency)

    override suspend fun insertAll(currencies: List<ExchangeRatesEntity>) {
        dao.insertAll(currencies)
    }

    override suspend fun update(currency: ExchangeRatesEntity) {
        dao.update(currency)
    }

    override suspend fun delete(currencyName: String) =
        dao.delete(currencyName)

    override suspend fun getFavourites(): List<ExchangeRatesEntity> =
        dao.getFavourites()
}
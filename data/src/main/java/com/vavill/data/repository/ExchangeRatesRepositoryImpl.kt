package com.vavill.data.repository

import android.util.Log
import com.vavill.data.db.entities.ExchangeRatesEntity
import com.vavill.data.mapper.ExchangeRatesMapper
import com.vavill.data.source.ExchangeRatesDbDataSource
import com.vavill.data.source.ExchangeRatesRemoteDataSource
import com.vavill.domain.model.ExchangeRatesModel
import com.vavill.domain.repository.ExchangeRatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class ExchangeRatesRepositoryImpl @Inject constructor(
    private val remoteDataSource: ExchangeRatesRemoteDataSource,
    private val dbDataSource: ExchangeRatesDbDataSource,
    private val mapper: ExchangeRatesMapper,
) : ExchangeRatesRepository {

    override suspend fun getExchangeRatesFromRemoteDataSource(
        baseCurrency: String,
    ): Flow<List<ExchangeRatesModel>> = channelFlow {
        val dbFlow = dbDataSource.getAllCurrenciesFromDb().distinctUntilChanged()
        var isApiCalled = false

        dbFlow.collectLatest { dbData ->
            if (!isApiCalled) {
                try {
                    val apiData = if (dbData.isEmpty() || dbData.all { it.fullName.isEmpty() }) {
                        mapper.mapRatesDTO(
                            remoteDataSource.getExchangeRatesFromApi(baseCurrency).rates,
                            getCurrenciesFullNamesFromRemoteDataSource()
                        )
                    } else {
                        mapper.mapRatesDTO(
                            remoteDataSource.getExchangeRatesFromApi(baseCurrency).rates,
                            getFullNames(dbData)
                        )
                    }

                    apiData.forEach { model ->
                        if(dbData.find { it.name == model.currencyName }?.isFavourite == true)
                            model.isFavourite = true
                    }

                    isApiCalled = true
                    dbDataSource.insertAll(apiData.map { mapper.mapModelToEntityDb(it) })
                    send(apiData)
                } catch (e: Exception) {
                    Log.d("myTag", e.message.toString())
                }
            }
        }
    }

    override suspend fun update(model: ExchangeRatesModel) {
        dbDataSource.update(mapper.mapModelToEntityDb(model))
    }

    override suspend fun getCurrenciesFullNamesFromRemoteDataSource(): Map<String, String> =
        remoteDataSource.getCurrenciesFullNamesFromApi().symbols

    override suspend fun insertCurrencyIntoDb(entity: ExchangeRatesModel) =
        dbDataSource.insert(mapper.mapModelToEntityDb(entity))

    override suspend fun deleteCurrencyFromDbDataSource(currencyName: String) =
        dbDataSource.delete(currencyName)

    override suspend fun getFavouriteCurrenciesFromDbDataSource(): List<ExchangeRatesModel> =
        dbDataSource.getFavourites().map(mapper::mapEntityDbToModel)

    private fun getFullNames(list: List<ExchangeRatesEntity>): Map<String, String> {
        val fullNames = mutableMapOf<String, String>()
        list.forEach {
            fullNames[it.name] = it.fullName
        }
        return fullNames
    }
}
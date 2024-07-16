package com.vavill.exchangerates.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.vavill.exchangerates.data.db.entities.ExchangeRatesEntity
import com.vavill.exchangerates.data.mapper.ExchangeRatesMapper
import com.vavill.exchangerates.data.source.ExchangeRatesDbDataSource
import com.vavill.exchangerates.data.source.ExchangeRatesRemoteDataSource
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import com.vavill.exchangerates.domain.repository.ExchangeRatesRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

class ExchangeRatesRepositoryImpl @Inject constructor(
    private val remoteDataSource: ExchangeRatesRemoteDataSource,
    private val dbDataSource: ExchangeRatesDbDataSource,
    private val mapper: ExchangeRatesMapper,
) : ExchangeRatesRepository {

    override suspend fun getExchangeRatesFromRemoteDataSource(
        baseCurrency: String?,
    ): Flow<List<ExchangeRatesModel>> {
        var apiData: List<ExchangeRatesModel> = listOf()
        val listFullNames = mutableMapOf<String, String>()
        return dbDataSource.getAllCurrenciesFromDb()
            .map { flow ->
                baseCurrency?.let { baseCurrency ->
                    flow.forEach {
                        if (it.fullName.isBlank()) {
                            listFullNames.clear()
                            return@forEach
                        } else
                            listFullNames[it.name] = it.fullName
                    }
                    if (flow.find { it.name == baseCurrency }?.rate != 1.0) {
                        Log.d("myTag", "getExchangeRatesFromRemoteDataSource: ${flow.find { it.name == baseCurrency }?.name}")
                        apiData = if (listFullNames.isNotEmpty())
                            mapper.mapRatesDTO(
                                remoteDataSource.getExchangeRatesFromApi(baseCurrency).rates,
                                listFullNames
                            )
                        else
                            mapper.mapRatesDTO(
                                remoteDataSource.getExchangeRatesFromApi(baseCurrency).rates,
                                getCurrenciesFullNamesFromRemoteDataSource()
                            )
                    }

                    apiData.forEach { model ->
                        if (flow.find { it.name == model.currencyName }?.isFavourite == true)
                            model.isFavourite = true
                        dbDataSource.insertCurrencyIntoDb(mapper.mapModelToEntityDb(model))
                    }
                }
                flow.map(mapper::mapEntityDbToModel)
            }
    }

    override suspend fun getCurrenciesFullNamesFromRemoteDataSource(): Map<String, String> =
        remoteDataSource.getCurrenciesFullNamesFromApi().symbols

//    override suspend fun getAllCurrenciesFromDbDataSource(): List<ExchangeRatesModel> =
//        dbDataSource.getAllCurrenciesFromDb().map(mapper::mapEntityDbToModel)

    override suspend fun insertCurrencyIntoDb(entity: ExchangeRatesModel) =
        dbDataSource.insertCurrencyIntoDb(mapper.mapModelToEntityDb(entity))

    override suspend fun deleteCurrencyFromDbDataSource(currencyName: String) =
        dbDataSource.deleteCurrencyFromDb(currencyName)

    override suspend fun getFavouriteCurrenciesFromDbDataSource(): List<ExchangeRatesModel> =
        dbDataSource.getFavouriteCurrenciesFromDb().map(mapper::mapEntityDbToModel)
}
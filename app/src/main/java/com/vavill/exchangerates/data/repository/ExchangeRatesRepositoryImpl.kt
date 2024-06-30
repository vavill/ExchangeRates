package com.vavill.exchangerates.data.repository

import android.graphics.Bitmap
import com.vavill.exchangerates.data.mapper.ExchangeRatesMapper
import com.vavill.exchangerates.data.source.ExchangeRatesDbDataSource
import com.vavill.exchangerates.data.source.ExchangeRatesRemoteDataSource
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import com.vavill.exchangerates.domain.repository.ExchangeRatesRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

class ExchangeRatesRepositoryImpl @Inject constructor(
    private val remoteDataSource: ExchangeRatesRemoteDataSource,
    private val dbDataSource: ExchangeRatesDbDataSource,
    private val mapper: ExchangeRatesMapper,
) : ExchangeRatesRepository {

    override suspend fun getExchangeRatesFromRemoteDataSource(
        baseCurrency: String,
    ): List<ExchangeRatesModel> {
        return coroutineScope {
            try {
                var apiData: List<ExchangeRatesModel> = listOf()
                val dbData = dbDataSource.getAllCurrenciesFromDb().map(mapper::mapEntityDbToModel)
                val listFullNames = mutableMapOf<String, String>()

                dbData.forEach {
                    if (it.currencyFullName.isBlank()) {
                        apiData = mapper.mapRatesDTO(
                            remoteDataSource.getExchangeRatesFromApi(baseCurrency).rates,
                            getCurrenciesFullNamesFromRemoteDataSource()
                        )
                        listFullNames.clear()
                        return@forEach
                    } else
                        listFullNames[it.currencyName] = it.currencyFullName
                }

                if (listFullNames.isNotEmpty())
                    apiData = mapper.mapRatesDTO(
                        remoteDataSource.getExchangeRatesFromApi(baseCurrency).rates,
                        listFullNames
                    )

                launch {
                    apiData.forEach { model ->
                        if (dbData.find { it.currencyName == model.currencyName }?.isFavourite == true)
                            model.isFavourite = true
                        dbDataSource.insertCurrencyIntoDb(mapper.mapModelToEntityDb(model))
                    }
                }.join()
            } catch (_: SocketTimeoutException) {

            }
            dbDataSource.getAllCurrenciesFromDb().map(mapper::mapEntityDbToModel)
        }
    }

    override suspend fun getCurrenciesFullNamesFromRemoteDataSource(): Map<String, String> =
        remoteDataSource.getCurrenciesFullNamesFromApi().symbols

    override suspend fun getAllCurrenciesFromDbDataSource(): List<ExchangeRatesModel> =
        dbDataSource.getAllCurrenciesFromDb().map(mapper::mapEntityDbToModel)

    override suspend fun insertCurrencyIntoDb(entity: ExchangeRatesModel) =
        dbDataSource.insertCurrencyIntoDb(mapper.mapModelToEntityDb(entity))

    override suspend fun deleteCurrencyFromDbDataSource(currencyName: String) =
        dbDataSource.deleteCurrencyFromDb(currencyName)

    override suspend fun getFavouriteCurrenciesFromDbDataSource(): List<ExchangeRatesModel> =
        dbDataSource.getFavouriteCurrenciesFromDb().map(mapper::mapEntityDbToModel)

    override suspend fun setCurrencyImage(currencyName: String, bitmap: Bitmap) =
        dbDataSource.setCurrencyImageIntoDb(currencyName, mapper.bitmapToByteArray(bitmap))
}
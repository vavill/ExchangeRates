package com.vavill.exchangerates.domain.interactor

import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import com.vavill.exchangerates.domain.repository.ExchangeRatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ExchangeRatesInteractor {
    suspend fun getExchangeRatesFromRepository(baseCurrency: String?): Flow<List<ExchangeRatesModel>>
  //  suspend fun getAllCurrenciesFromRepository(): List<ExchangeRatesModel>
    suspend fun insertCurrencyIntoRepository(model: ExchangeRatesModel)
    suspend fun deleteCurrencyFromRepository(currencyName: String)
    suspend fun getFavouriteCurrenciesFromRepository(): List<ExchangeRatesModel>
    suspend fun filterSearchData(filter: String): Flow<List<ExchangeRatesModel>>
}

class ExchangeRatesInteractorImpl @Inject constructor(
    private val repository: ExchangeRatesRepository
) : ExchangeRatesInteractor {

    override suspend fun getExchangeRatesFromRepository(
        baseCurrency: String?
    ): Flow<List<ExchangeRatesModel>> =
        repository.getExchangeRatesFromRemoteDataSource(baseCurrency)

//    override suspend fun getAllCurrenciesFromRepository(): List<ExchangeRatesModel> =
//        repository.getAllCurrenciesFromDbDataSource()

    override suspend fun insertCurrencyIntoRepository(model: ExchangeRatesModel) =
        repository.insertCurrencyIntoDb(model)

    override suspend fun deleteCurrencyFromRepository(currencyName: String) =
        repository.deleteCurrencyFromDbDataSource(currencyName)

    override suspend fun getFavouriteCurrenciesFromRepository(): List<ExchangeRatesModel> =
        repository.getFavouriteCurrenciesFromDbDataSource()

    override suspend fun filterSearchData(
        filter: String
    ): Flow<List<ExchangeRatesModel>> =
        if (filter.isBlank())
            getExchangeRatesFromRepository(null)
        else
            getExchangeRatesFromRepository(null).map {
                it.filter {
                    it.currencyName.contains(filter, true)
                            || it.currencyFullName.contains(filter, true)
                }

                    .sortedWith(compareBy<ExchangeRatesModel> { it.currencyName }
                        .thenBy { it.currencyFullName })
                    .reversed()
            }
}
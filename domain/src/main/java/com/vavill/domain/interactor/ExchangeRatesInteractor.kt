package com.vavill.domain.interactor

import com.vavill.domain.model.ExchangeRatesModel
import com.vavill.domain.repository.ExchangeRatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ExchangeRatesInteractor {
    suspend fun getExchangeRates(baseCurrency: String): Flow<List<ExchangeRatesModel>>
    suspend fun insertCurrency(model: ExchangeRatesModel)
    suspend fun deleteCurrency(currencyName: String)
    suspend fun getFavouriteCurrencies(): List<ExchangeRatesModel>
    suspend fun filterSearchData(filter: String, baseCurrency: String): Flow<List<ExchangeRatesModel>>
    suspend fun update(model: ExchangeRatesModel)
}

class ExchangeRatesInteractorImpl @Inject constructor(
    private val repository: ExchangeRatesRepository
) : ExchangeRatesInteractor {

    override suspend fun getExchangeRates(
        baseCurrency: String
    ): Flow<List<ExchangeRatesModel>> =
        repository.getExchangeRatesFromRemoteDataSource(baseCurrency)

    override suspend fun insertCurrency(model: ExchangeRatesModel) =
        repository.insertCurrencyIntoDb(model)

    override suspend fun deleteCurrency(currencyName: String) =
        repository.deleteCurrencyFromDbDataSource(currencyName)

    override suspend fun getFavouriteCurrencies(): List<ExchangeRatesModel> =
        repository.getFavouriteCurrenciesFromDbDataSource()

    override suspend fun update(model: ExchangeRatesModel) {
        repository.update(model)
    }

    override suspend fun filterSearchData(
        filter: String,
        baseCurrency: String
    ): Flow<List<ExchangeRatesModel>> =
        if (filter.isBlank())
            getExchangeRates(baseCurrency)
        else
            getExchangeRates(baseCurrency).map { model ->
                model.filter {
                    it.currencyName.contains(filter, true)
                            || it.currencyFullName.contains(filter, true)
                }
                    .sortedWith(compareBy<ExchangeRatesModel> { it.currencyName }
                    .thenBy { it.currencyFullName })
                    .reversed()
            }
}
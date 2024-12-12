package com.vavill.common.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vavill.domain.interactor.ExchangeRatesInteractor
import com.vavill.domain.model.ExchangeRatesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeRatesViewModel @Inject constructor(
    private val interactor: ExchangeRatesInteractor,
) : ViewModel() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "baseCurrency")
    private val datastoreKey = stringPreferencesKey("BaseCurrency")

    private val _exchangeRatesStateFlow = MutableStateFlow<List<ExchangeRatesModel>?>(null)
    val exchangeRatesStateFlow: StateFlow<List<ExchangeRatesModel>?> get() = _exchangeRatesStateFlow.asStateFlow()

    private val _currentSortTypeStateFlow = MutableStateFlow(SortType.ALPHA_ASC)
    val currentSortTypeStateFlow: StateFlow<SortType> get() = _currentSortTypeStateFlow

    private val _filteredSearchDataStateFlow = MutableStateFlow<List<ExchangeRatesModel>?>(null)
    val filteredSearchDataStateFlow: StateFlow<List<ExchangeRatesModel>?> get() = _filteredSearchDataStateFlow.asStateFlow()

    private val _favouriteCurrenciesStateFlow = MutableStateFlow<List<ExchangeRatesModel>?>(null)
    val favouriteCurrenciesStateFlow: StateFlow<List<ExchangeRatesModel>?>
        get() = _favouriteCurrenciesStateFlow.asStateFlow()

    private val _baseCurrencyStateFlow = MutableStateFlow<String?>(null)
    val baseCurrencyStateFlow: StateFlow<String?> get() = _baseCurrencyStateFlow.asStateFlow()

    fun loadExchangeRatesFromApi() {
        viewModelScope.launch {
            interactor.getExchangeRates(baseCurrencyStateFlow.value!!)
                .onEach {
                    _exchangeRatesStateFlow.value = it
                    setSortType(_currentSortTypeStateFlow.value)
                }
                .launchIn(viewModelScope)
        }
    }

    suspend fun getBaseCurrencyFromDataStore(context: Context) {
        val preferences = context.dataStore.data.first()
        setBaseCurrency(preferences[datastoreKey] ?: "RUB")
    }

    suspend fun saveBaseCurrencyToDataStore(context: Context, baseCurrency: String) {
        context.dataStore.edit {
            it[datastoreKey] = baseCurrency
        }
    }

    fun setBaseCurrency(baseCurrency: String) {
        _baseCurrencyStateFlow.value = baseCurrency
    }

    fun getFavouriteCurrencies() {
        flow {
            emit(interactor.getFavouriteCurrencies())
        }.onEach {
            _favouriteCurrenciesStateFlow.value = it
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun insertCurrencyIntoDb(model: ExchangeRatesModel) {
        flow {
            emit(interactor.insertCurrency(model))
        }
            .onEach {
                getFavouriteCurrencies()
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun updateCurrency(model: ExchangeRatesModel) {
        viewModelScope.launch {
            interactor.update(model)
        }
    }

    fun setSortType(sortType: SortType) {
        _currentSortTypeStateFlow.value = sortType
        _exchangeRatesStateFlow.value = when (sortType) {
            SortType.ALPHA_ASC -> _exchangeRatesStateFlow.value?.sortedBy { it.currencyName }
            SortType.ALPHA_DESC -> _exchangeRatesStateFlow.value?.sortedByDescending { it.currencyName }
            SortType.NUMERIC_ASC -> _exchangeRatesStateFlow.value?.sortedBy { it.currencyValue }
            SortType.NUMERIC_DESC -> _exchangeRatesStateFlow.value?.sortedByDescending { it.currencyValue }
        }
    }

    fun setFilterSearch(filter: String) {
        viewModelScope.launch {
            _filteredSearchDataStateFlow.value = getFilteredSearchData(filter)
        }
    }

    private suspend fun getFilteredSearchData(
        filter: String
    ): List<ExchangeRatesModel> =
        interactor.filterSearchData(filter, baseCurrencyStateFlow.value!!)
            .flowOn(Dispatchers.IO)
            .first()
}

enum class SortType {
    ALPHA_ASC,
    ALPHA_DESC,
    NUMERIC_ASC,
    NUMERIC_DESC
}
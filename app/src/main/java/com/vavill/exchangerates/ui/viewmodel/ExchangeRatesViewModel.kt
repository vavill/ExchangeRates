package com.vavill.exchangerates.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vavill.exchangerates.domain.interactor.ExchangeRatesInteractor
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import com.vavill.exchangerates.ui.fragment.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
    private val DATASTORE_KEY = stringPreferencesKey("BaseCurrency")

    private val _exchangeRatesStateFlow = MutableStateFlow<List<ExchangeRatesModel>?>(null)
    val exchangeRatesStateFlow: StateFlow<List<ExchangeRatesModel>?> get() = _exchangeRatesStateFlow.asStateFlow()

    private val _currentSortTypeStateFlow = MutableStateFlow(SortType.ALPHA_DESC)
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
            Log.d("myTag", "loadExchangeRatesFromApi")
            interactor.getExchangeRatesFromRepository(baseCurrencyStateFlow.value!!)
                .onEach {
                    Log.d("myTag", "loadExchangeRatesFromApi: ${it.size}")
                    if (_exchangeRatesStateFlow.value == it)
                        Log.d("myTag", "_exchangeRatesStateFlow.value == it")
                    _exchangeRatesStateFlow.value = it
                }
                .catch {
                    Log.d("myTag", "catch ${it.stackTraceToString()}")
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }
    }

    suspend fun getBaseCurrencyFromDataStore(context: Context) {
        val preferences = context.dataStore.data.first()
        setBaseCurrency(preferences[DATASTORE_KEY] ?: "RUB")
    }

    suspend fun saveBaseCurrencyToDataStore(context: Context, baseCurrency: String) {
        context.dataStore.edit {
            it[DATASTORE_KEY] = baseCurrency
        }
    }

    fun setBaseCurrency(baseCurrency: String) {
        _baseCurrencyStateFlow.value = baseCurrency
    }

    fun getFavouriteCurrencies() {
        flow {
            emit(interactor.getFavouriteCurrenciesFromRepository())
        }.onEach {
            _favouriteCurrenciesStateFlow.value = it
        }.flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

//    private fun getAllCurrenciesFromDb() {
//        viewModelScope.launch {
//            _exchangeRatesStateFlow.value = interactor.getAllCurrenciesFromRepository()
//        }
//    }

    fun insertCurrencyIntoDb(model: ExchangeRatesModel) {
        flow {
            emit(interactor.insertCurrencyIntoRepository(model))
        }
            .onEach {
              //  getAllCurrenciesFromDb()
                getFavouriteCurrencies()
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun setCurrentSortType(sortType: SortType) {
        _currentSortTypeStateFlow.value = sortType
    }

    fun setFilterSearch(filter: String) {
        viewModelScope.launch {
            _filteredSearchDataStateFlow.value = getFilteredSearchData(filter)
        }
    }

    private suspend fun getFilteredSearchData(
        filter: String
    ): List<ExchangeRatesModel> =
        interactor.filterSearchData(filter)
        .flowOn(Dispatchers.IO)
            .first()
}
package com.vavill.exchangerates.ui.viewmodel

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.vavill.exchangerates.R
import com.vavill.exchangerates.domain.interactor.ExchangeRatesInteractor
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import com.vavill.exchangerates.ui.fragment.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class ExchangeRatesViewModel @Inject constructor(
    private val interactor: ExchangeRatesInteractor
) : ViewModel() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "baseCurrency")
    private val DATASTORE_KEY = stringPreferencesKey("BaseCurrency")

    private val _exchangeRatesLiveData = MutableLiveData<List<ExchangeRatesModel>>()
    val exchangeRatesLiveData: LiveData<List<ExchangeRatesModel>> get() = _exchangeRatesLiveData

    private val _currentSortTypeLiveData = MutableLiveData(SortType.ALPHA_ASC)
    val currentSortTypeLiveData: LiveData<SortType> get() = _currentSortTypeLiveData

    private val _querySearchLiveData = MutableLiveData<String>()
    val querySearchLiveData: LiveData<String> get() = _querySearchLiveData

    private val _favouriteCurrenciesLiveData = MutableLiveData<List<ExchangeRatesModel>>()
    val favouriteCurrenciesLiveData: LiveData<List<ExchangeRatesModel>> get() = _favouriteCurrenciesLiveData

    private val _baseCurrencyLiveData = MutableLiveData<String>()
    val baseCurrencyLiveData: LiveData<String> get() = _baseCurrencyLiveData

    fun loadExchangeRatesFromApi() {
        viewModelScope.launch {
            _exchangeRatesLiveData.value =
                interactor.getExchangeRatesFromRepository(baseCurrencyLiveData.value!!)
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
        _baseCurrencyLiveData.value = baseCurrency
    }

    fun getFavouriteCurrencies() {
        viewModelScope.launch {
            _favouriteCurrenciesLiveData.value = interactor.getFavouriteCurrenciesFromRepository()
        }
    }

    fun getAllCurrenciesFromDb() {
        viewModelScope.launch {
            _exchangeRatesLiveData.value = interactor.getAllCurrenciesFromRepository()
        }
    }

    fun insertCurrencyIntoDb(model: ExchangeRatesModel) {
        viewModelScope.launch {
            interactor.insertCurrencyIntoRepository(model)
        }
    }

    fun setCurrentSortType(sortType: SortType) {
        _currentSortTypeLiveData.value = sortType
    }

    fun setFilterSearch(filterString: String) {
        _querySearchLiveData.value = filterString
    }

    fun setCurrencyImage(currencyName: String, imageView: ImageView, imageLoader: ImageLoader) {
        viewModelScope.launch {

            val url = "https://raw.githubusercontent.com/Lissy93/" +
                    "currency-flags/master/assets/flags_svg/$currencyName.svg"

//            val url = "https://i.pinimg.com/736x/c7/95/56/c7955627ba471fda2aa800cea350e58e.jpg"

            try {
                withTimeout(5000L) {
                    imageView.load(url, imageLoader) {
                        placeholder(R.drawable.ic_launcher_background)
                        error(R.drawable.ic_favourite_true)
                        scale(Scale.FIT)
                        transformations(CircleCropTransformation())
                    }
                }
            } catch (e: Exception) {
                Log.e("ImageLoadError", "Error loading image: ${e.message}")
            }

//            interactor.setCurrencyImageIntoRepository(currencyName, imageView.drawable.toBitmap())
        }
    }
}
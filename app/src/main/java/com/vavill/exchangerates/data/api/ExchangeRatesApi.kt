package com.vavill.exchangerates.data.api

import com.vavill.exchangerates.data.dto.ExchangeRatesResponse
import com.vavill.exchangerates.data.dto.SymbolsDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRatesApi {

    @GET("latest")
    suspend fun getExchangeRates(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("base") baseCurrency: String = "RUB"
    ): ExchangeRatesResponse

    @GET("symbols")
    suspend fun getCurrenciesFullNames(
        @Query("apikey") apiKey: String = API_KEY
    ): SymbolsDTO

    companion object {
        private const val API_KEY = "J0IYLbNf87VLRqCTOyVlvn1DmNb1c382"
        // u8UxQbS98BOJA8AxTdYg15d0Jk4MroOg
        // Zt1vBM2daYF6HOjZz15Y818C0Qn4s7og
        // krgPW5BCaXSX8Yfn2Ldp3UKLeHlb6Fnu
        // w0IFxB35WCc2tjgyWTaE1HFyFR6qC8Zr
    }
}
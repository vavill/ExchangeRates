package com.vavill.data.api

import com.vavill.data.dto.ExchangeRatesResponse
import com.vavill.data.dto.SymbolsDTO
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
        private const val API_KEY = "krgPW5BCaXSX8Yfn2Ldp3UKLeHlb6Fnu"
        // Zt1vBM2daYF6HOjZz15Y818C0Qn4s7og
        // w0IFxB35WCc2tjgyWTaE1HFyFR6qC8Zr
        // u8UxQbS98BOJA8AxTdYg15d0Jk4MroOg
        // J0IYLbNf87VLRqCTOyVlvn1DmNb1c382
    }
}
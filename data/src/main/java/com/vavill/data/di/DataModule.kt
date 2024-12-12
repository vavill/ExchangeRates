package com.vavill.data.di

import android.content.Context
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import com.vavill.data.db.ExchangeRatesDatabase
import com.vavill.data.db.dao.ExchangeRatesDao
import com.vavill.data.mapper.ExchangeRatesMapper
import com.vavill.data.repository.ExchangeRatesRepositoryImpl
import com.vavill.data.source.ExchangeRatesDbDataSource
import com.vavill.data.source.ExchangeRatesRemoteDataSource
import com.vavill.domain.repository.ExchangeRatesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.apilayer.com/exchangerates_data/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    fun provideExchangeRatesApi(retrofit: Retrofit): com.vavill.data.api.ExchangeRatesApi {
        return retrofit.create(com.vavill.data.api.ExchangeRatesApi::class.java)
    }

    @Provides
    fun provideExchangeRatesRepository(
        remoteDataSource: ExchangeRatesRemoteDataSource,
        dbDataSource: ExchangeRatesDbDataSource,
        mapper: ExchangeRatesMapper,
    ): ExchangeRatesRepository {
        return ExchangeRatesRepositoryImpl(
            remoteDataSource,
            dbDataSource,
            mapper
        )
    }

    @Provides
    fun provideExchangeRatesRemoteDataSource(api: com.vavill.data.api.ExchangeRatesApi): ExchangeRatesRemoteDataSource {
        return com.vavill.data.source.ExchangeRatesRemoteDataSourceImpl(api)
    }

    @Provides
    fun provideExchangeRatesMapper(): ExchangeRatesMapper =
        ExchangeRatesMapper()

    @Provides
    fun provideExchangeRatesDbDataSource(dao: ExchangeRatesDao): ExchangeRatesDbDataSource {
        return com.vavill.data.source.ExchangeRatesDbDataSourceImpl(dao)
    }

    @Provides
    fun provideExchangeRatesDao(database: ExchangeRatesDatabase): ExchangeRatesDao {
        return database.getExchangeRatesDao()
    }

    @Provides
    fun provideExchangeRatesDatabase(context: Context): ExchangeRatesDatabase {
        return ExchangeRatesDatabase.create(context)
    }

}
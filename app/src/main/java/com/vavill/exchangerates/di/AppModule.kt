package com.vavill.exchangerates.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vavill.exchangerates.app.App
import com.vavill.exchangerates.data.api.ExchangeRatesApi
import com.vavill.exchangerates.data.db.ExchangeRatesDatabase
import com.vavill.exchangerates.data.db.dao.ExchangeRatesDao
import com.vavill.exchangerates.data.mapper.ExchangeRatesMapper
import com.vavill.exchangerates.data.repository.ExchangeRatesRepositoryImpl
import com.vavill.exchangerates.data.source.ExchangeRatesDbDataSource
import com.vavill.exchangerates.data.source.ExchangeRatesDbDataSourceImpl
import com.vavill.exchangerates.data.source.ExchangeRatesRemoteDataSource
import com.vavill.exchangerates.data.source.ExchangeRatesRemoteDataSourceImpl
import com.vavill.exchangerates.domain.interactor.ExchangeRatesInteractor
import com.vavill.exchangerates.domain.interactor.ExchangeRatesInteractorImpl
import com.vavill.exchangerates.domain.repository.ExchangeRatesRepository
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
class AppModule {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context {
        return App.getInstance().applicationContext
    }

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
    fun provideExchangeRatesApi(retrofit: Retrofit): ExchangeRatesApi {
        return retrofit.create(ExchangeRatesApi::class.java)
    }

    @Provides
    fun provideExchangeRatesRemoteDataSource(api: ExchangeRatesApi): ExchangeRatesRemoteDataSource {
        return ExchangeRatesRemoteDataSourceImpl(api)
    }

    @Provides
    fun provideExchangeRatesDbDataSource(dao: ExchangeRatesDao): ExchangeRatesDbDataSource {
        return ExchangeRatesDbDataSourceImpl(dao)
    }

    @Provides
    fun provideExchangeRatesMapper(): ExchangeRatesMapper = ExchangeRatesMapper()

    @Provides
    fun provideExchangeRatesRepository(
        remoteDataSource: ExchangeRatesRemoteDataSource,
        dbDataSource: ExchangeRatesDbDataSource,
        mapper: ExchangeRatesMapper,
    ): ExchangeRatesRepository {
        return ExchangeRatesRepositoryImpl(remoteDataSource, dbDataSource, mapper)
    }

    @Provides
    fun provideExchangeRatesInteractor(repository: ExchangeRatesRepository): ExchangeRatesInteractor {
        return ExchangeRatesInteractorImpl(repository)
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
package com.vavill.domain.di

import com.vavill.domain.interactor.ExchangeRatesInteractor
import com.vavill.domain.interactor.ExchangeRatesInteractorImpl
import com.vavill.domain.repository.ExchangeRatesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {

    @Provides
    fun provideExchangeRatesInteractor(repository: ExchangeRatesRepository): ExchangeRatesInteractor {
        return ExchangeRatesInteractorImpl(repository)
    }




}
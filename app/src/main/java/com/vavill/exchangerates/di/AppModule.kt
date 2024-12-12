package com.vavill.exchangerates.di

import android.content.Context
import com.vavill.data.di.DataModule
import com.vavill.domain.di.DomainModule
import com.vavill.exchangerates.app.App
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [DataModule::class, DomainModule::class])
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context {
        return App.getInstance().applicationContext
    }
}
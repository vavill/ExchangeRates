package com.vavill.exchangerates.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
    }

    companion object {
        private lateinit var application: App

        fun getInstance(): App {
            return application
        }
    }
}
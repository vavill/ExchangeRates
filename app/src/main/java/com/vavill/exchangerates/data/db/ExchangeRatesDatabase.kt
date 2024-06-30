package com.vavill.exchangerates.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vavill.exchangerates.data.db.dao.ExchangeRatesDao
import com.vavill.exchangerates.data.db.entities.ExchangeRatesEntity

@Database(entities = [ExchangeRatesEntity::class], version = 1)
abstract class ExchangeRatesDatabase: RoomDatabase() {
    abstract fun getExchangeRatesDao(): ExchangeRatesDao

    companion object {
        private const val EXCHANGE_RATES_DATABASE_NAME = "exchangeRatesDatabase"

        fun create(applicationContext: Context): ExchangeRatesDatabase =
            Room.databaseBuilder(
                applicationContext,
                ExchangeRatesDatabase::class.java,
                EXCHANGE_RATES_DATABASE_NAME
            ).build()
    }
}
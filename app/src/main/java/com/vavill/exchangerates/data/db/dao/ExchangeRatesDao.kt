package com.vavill.exchangerates.data.db.dao

import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vavill.exchangerates.data.db.entities.ExchangeRatesEntity

@Dao
interface ExchangeRatesDao {

    @Query("SELECT * FROM Currencies ")
    suspend fun getAllCurrencies(): List<ExchangeRatesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ExchangeRatesEntity::class)
    suspend fun insertCurrency(entity: ExchangeRatesEntity)

    @Query("DELETE FROM Currencies WHERE currencyName is :currencyName")
    suspend fun deleteCurrency(currencyName: String)

    @Query("SELECT * FROM Currencies WHERE isFavourite = 1")
    suspend fun getFavouriteCurrencies(): List<ExchangeRatesEntity>

    @Query("UPDATE Currencies SET currencyImage = :image WHERE currencyName = :currencyName")
    suspend fun setCurrencyImage(currencyName: String, image: ByteArray)
}
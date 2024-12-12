package com.vavill.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vavill.data.db.entities.ExchangeRatesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRatesDao {

    @Query("SELECT * FROM Currencies ")
    fun getAllCurrencies(): Flow<List<ExchangeRatesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ExchangeRatesEntity::class)
    suspend fun insert(currency: ExchangeRatesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ExchangeRatesEntity::class)
    suspend fun insertAll(currencies: List<ExchangeRatesEntity>)

    @Update(entity = ExchangeRatesEntity::class)
    suspend fun update(currency: ExchangeRatesEntity)

    @Query("DELETE FROM Currencies WHERE currencyName is :currencyName")
    suspend fun delete(currencyName: String)

    @Query("SELECT * FROM Currencies WHERE isFavourite = 1")
    suspend fun getFavourites(): List<ExchangeRatesEntity>
}
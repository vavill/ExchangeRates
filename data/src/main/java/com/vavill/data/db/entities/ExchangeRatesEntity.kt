package com.vavill.data.db.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Currencies")
data class ExchangeRatesEntity(

    @PrimaryKey
    @ColumnInfo(name = "currencyName")
    val name: String,

    @ColumnInfo(name = "currencyFullName")
    val fullName: String,

    @ColumnInfo(name = "currencyRate")
    val rate: Double,

    @ColumnInfo(name = "isFavourite")
    val isFavourite: Boolean
)
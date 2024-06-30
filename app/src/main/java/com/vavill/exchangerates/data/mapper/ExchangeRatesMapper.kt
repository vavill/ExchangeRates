package com.vavill.exchangerates.data.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.vavill.exchangerates.data.db.entities.ExchangeRatesEntity
import com.vavill.exchangerates.data.dto.RatesDTO
import com.vavill.exchangerates.data.dto.SymbolsDTO
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import java.io.ByteArrayOutputStream
import kotlin.reflect.full.memberProperties

class ExchangeRatesMapper {

    fun mapRatesDTO(
        rates: Map<String, Double>,
        fullNames: Map<String, String>
    ): List<ExchangeRatesModel> = getExchangeRatesList(rates, fullNames)

    fun mapModelToEntityDb(model: ExchangeRatesModel): ExchangeRatesEntity {
        return ExchangeRatesEntity(
            model.currencyName,
            model.currencyFullName,
            model.currencyValue,
            model.isFavourite,
            bitmapToByteArray(model.image)
        )
    }

    fun mapEntityDbToModel(entity: ExchangeRatesEntity): ExchangeRatesModel {
        return ExchangeRatesModel(
            entity.name,
            entity.rate,
            entity.fullName,
            entity.isFavourite,
            byteArrayToBitmap(entity.image),
        )
    }

    private fun getExchangeRatesList(
        rates: Map<String, Double>,
        fullNames: Map<String, String>
    ): List<ExchangeRatesModel> {
        val list = mutableListOf<ExchangeRatesModel>()
        rates.forEach { (name, value) ->
            list.add(
                ExchangeRatesModel(
                    name,
                    value,
                    fullNames.getValue(name),
                    false,
                    image = null
                )
            )
        }
        return list
    }

    fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
        return byteArray?.let {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }

    }
}
package com.vavill.data.mapper

import com.vavill.data.db.entities.ExchangeRatesEntity
import com.vavill.domain.model.ExchangeRatesModel

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
        )
    }

    fun mapEntityDbToModel(entity: ExchangeRatesEntity): ExchangeRatesModel {
        return ExchangeRatesModel(
            entity.name,
            entity.rate,
            entity.fullName,
            entity.isFavourite,
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
                    fullNames[name] ?: "",
                    false,
                )
            )
        }
        return list
    }
}
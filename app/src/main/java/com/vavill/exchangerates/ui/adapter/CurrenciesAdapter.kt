package com.vavill.exchangerates.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.ImageLoader
import com.vavill.exchangerates.R
import com.vavill.exchangerates.databinding.ItemCurrencyBinding
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import com.vavill.exchangerates.ui.fragment.CurrenciesFragment
import com.vavill.exchangerates.ui.fragment.ExchangeRatesFragment
import com.vavill.exchangerates.ui.fragment.SortType
import com.vavill.exchangerates.ui.viewmodel.ExchangeRatesViewModel
import java.util.Locale

class CurrenciesAdapter(
    private val getImageLoader: GetImageLoader,
    private val viewModel: ExchangeRatesViewModel,
) : RecyclerView.Adapter<CurrenciesAdapter.ExchangeRatesViewHolder>() {

    private var currenciesList = listOf<ExchangeRatesModel>()

    fun setData(list: List<ExchangeRatesModel>) {
        currenciesList = list
        sortData(SortType.ALPHA_ASC)
        notifyDataSetChanged()
    }

    fun sortData(sortType: SortType) {
        currenciesList = when (sortType) {
            SortType.ALPHA_ASC -> currenciesList.sortedBy { it.currencyName }
            SortType.ALPHA_DESC -> currenciesList.sortedByDescending { it.currencyName }
            SortType.NUMERIC_ASC -> currenciesList.sortedBy { it.currencyValue }
            SortType.NUMERIC_DESC -> currenciesList.sortedByDescending { it.currencyValue }
        }
        notifyDataSetChanged()
    }

    fun filterData(filter: String) {
        if (filter.isBlank())
            setData(viewModel.exchangeRatesLiveData.value!!)
        else {
            val filteredList = viewModel.exchangeRatesLiveData.value!!.filter {
                it.currencyName.contains(filter, true)
                        || it.currencyFullName.contains(filter, true)
            }
                .sortedWith(compareBy<ExchangeRatesModel> { it.currencyName }
                    .thenBy { it.currencyFullName })
                .reversed()

            setData(filteredList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRatesViewHolder =
        ExchangeRatesViewHolder(
            ItemCurrencyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ExchangeRatesViewHolder, position: Int) {
        holder.view.animation = android.view.animation.AnimationUtils.loadAnimation(
            holder.view.context,
            R.anim.anim_recycler_view_items
        )
        holder.bind(currenciesList[position])
    }

    override fun getItemCount(): Int = currenciesList.size

    inner class ExchangeRatesViewHolder(
        private val binding: ItemCurrencyBinding
    ) : ViewHolder(binding.root) {
        val view = binding.root

        fun bind(item: ExchangeRatesModel) {
            binding.currencyNameTextView.text = item.currencyName
            binding.currencyFullNameTextView.text = item.currencyFullName
            binding.currencyValueTextView.text = String.format(
                Locale.US,
                "%.3f",
                item.currencyValue
            )

            when (item.isFavourite) {
                true -> binding.currencyFavouriteImageView.setImageResource(R.drawable.ic_favourite_true)
                false -> binding.currencyFavouriteImageView.setImageResource(R.drawable.ic_favourite_false)
            }

//            viewModel.setCurrencyImage(
//                item.currencyName,
//                binding.flagImageView,
//                getImageLoader.loadImageFromUrl()
//            )
            setItemOnClickListener(item)
            setFavouriteOnClickListener(item)
        }

        private fun setItemOnClickListener(item: ExchangeRatesModel) {
            binding.root.setOnClickListener {
                viewModel.setBaseCurrency(item.currencyName)
                viewModel.loadExchangeRatesFromApi()
                notifyDataSetChanged()
            }
        }

        private fun setFavouriteOnClickListener(item: ExchangeRatesModel) {
            binding.currencyFavouriteImageView.setOnClickListener {
                item.isFavourite = !item.isFavourite
                viewModel.insertCurrencyIntoDb(item)
                viewModel.getFavouriteCurrencies()
                notifyItemChanged(currenciesList.indexOf(item))
            }
        }
    }
}

interface GetImageLoader {
    fun loadImageFromUrl(): ImageLoader
}
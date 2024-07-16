package com.vavill.exchangerates.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vavill.exchangerates.R
import com.vavill.exchangerates.databinding.ItemCurrencyBinding
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import com.vavill.exchangerates.ui.fragment.SortType
import java.util.Locale

class CurrenciesAdapter(
    private val onClickListenerCurrencies: OnClickListenerCurrencies
) : ListAdapter<ExchangeRatesModel, CurrenciesAdapter.ExchangeRatesViewHolder>(ExchangeRatesDiffCallback()) {

    fun sortData(sortType: SortType) {
        Log.d("myTag", "sortData: $sortType")
        submitList(when (sortType) {
            SortType.ALPHA_ASC -> currentList.sortedBy { it.currencyName }
            SortType.ALPHA_DESC -> currentList.sortedByDescending { it.currencyName }
            SortType.NUMERIC_ASC -> currentList.sortedBy { it.currencyValue }
            SortType.NUMERIC_DESC -> currentList.sortedByDescending { it.currencyValue }
        })
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
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

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

            setItemOnClickListener(item)
            setFavouriteOnClickListener(item)
        }

        private fun setItemOnClickListener(item: ExchangeRatesModel) {
            binding.root.setOnClickListener {
                onClickListenerCurrencies.itemOnClickListener(item)
            }
        }

        private fun setFavouriteOnClickListener(item: ExchangeRatesModel) {
            binding.currencyFavouriteImageView.setOnClickListener {
                item.isFavourite = !item.isFavourite
                onClickListenerCurrencies.favouriteOnClickListener(item)
                notifyItemChanged(currentList.indexOf(item))
            }
        }
    }

    class ExchangeRatesDiffCallback: DiffUtil.ItemCallback<ExchangeRatesModel>() {
        override fun areItemsTheSame(
            oldItem: ExchangeRatesModel,
            newItem: ExchangeRatesModel
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ExchangeRatesModel,
            newItem: ExchangeRatesModel
        ): Boolean =
            oldItem.currencyName == newItem.currencyName &&
            oldItem.currencyFullName == newItem.currencyFullName &&
            oldItem.currencyValue == newItem.currencyValue &&
            oldItem.isFavourite == newItem.isFavourite
    }
}

interface OnClickListenerCurrencies {
    fun favouriteOnClickListener(item: ExchangeRatesModel)
    fun itemOnClickListener(item: ExchangeRatesModel)
}
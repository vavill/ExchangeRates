package com.vavill.ui.xml.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vavill.common.viewmodels.SortType
import com.vavill.ui.xml.R
import com.vavill.ui.xml.databinding.ItemCurrencyBinding
import java.util.Locale

class CurrenciesAdapter(
    private val onClickListenerCurrencies: OnClickListenerCurrencies
) : ListAdapter<com.vavill.domain.model.ExchangeRatesModel, CurrenciesAdapter.ExchangeRatesViewHolder>(
    ExchangeRatesDiffCallback()
) {

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

        fun bind(item: com.vavill.domain.model.ExchangeRatesModel) {
            binding.currencyNameTextView.text = item.currencyName
            binding.currencyFullNameTextView.text = item.currencyFullName
            binding.currencyValueTextView.text = String.format(
                Locale.US,
                "%.3f",
                item.currencyValue
            )

            when (item.isFavourite) {
                true -> binding.currencyFavouriteImageView.setImageResource(
                    com.vavill.common.R.drawable.ic_favourite_true
                )
                false -> binding.currencyFavouriteImageView.setImageResource(
                    com.vavill.common.R.drawable.ic_favourite_false
                )
            }

            setItemOnClickListener(item)
            setFavouriteOnClickListener(item)
        }

        private fun setItemOnClickListener(item: com.vavill.domain.model.ExchangeRatesModel) {
            binding.root.setOnClickListener {
                onClickListenerCurrencies.itemOnClickListener(item)
            }
        }

        private fun setFavouriteOnClickListener(item: com.vavill.domain.model.ExchangeRatesModel) {
            binding.currencyFavouriteImageView.setOnClickListener {
                item.isFavourite = !item.isFavourite
                onClickListenerCurrencies.favouriteOnClickListener(item)
                notifyItemChanged(currentList.indexOf(item))
            }
        }
    }

    class ExchangeRatesDiffCallback :
        DiffUtil.ItemCallback<com.vavill.domain.model.ExchangeRatesModel>() {
        override fun areItemsTheSame(
            oldItem: com.vavill.domain.model.ExchangeRatesModel,
            newItem: com.vavill.domain.model.ExchangeRatesModel
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: com.vavill.domain.model.ExchangeRatesModel,
            newItem: com.vavill.domain.model.ExchangeRatesModel
        ): Boolean =
            oldItem.currencyName == newItem.currencyName &&
            oldItem.currencyFullName == newItem.currencyFullName &&
            oldItem.currencyValue == newItem.currencyValue &&
            oldItem.isFavourite == newItem.isFavourite
    }
}

interface OnClickListenerCurrencies {
    fun favouriteOnClickListener(item: com.vavill.domain.model.ExchangeRatesModel)
    fun itemOnClickListener(item: com.vavill.domain.model.ExchangeRatesModel)
}
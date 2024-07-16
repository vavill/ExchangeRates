package com.vavill.exchangerates.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vavill.exchangerates.R
import com.vavill.exchangerates.databinding.ItemCurrencyBinding
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import com.vavill.exchangerates.ui.adapter.CurrenciesAdapter.ExchangeRatesDiffCallback
import java.util.Locale

class FavouritesAdapter(
    private val onClickListener: OnClickListenerFavourites
) : ListAdapter<ExchangeRatesModel, FavouritesAdapter.FavouritesViewHolder>(ExchangeRatesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        return FavouritesViewHolder(
            ItemCurrencyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        holder.view.animation = android.view.animation.AnimationUtils.loadAnimation(
            holder.view.context,
            R.anim.anim_recycler_view_items
        )
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    inner class FavouritesViewHolder(
        private val binding: ItemCurrencyBinding
    ): ViewHolder(binding.root) {
        val view = binding.root

        fun bind(item: ExchangeRatesModel) {
            binding.currencyNameTextView.text = item.currencyName
            binding.currencyFullNameTextView.text = item.currencyFullName
            binding.currencyValueTextView.text = String.format(
                Locale.US,
                "%.3f",
                item.currencyValue)

            when (item.isFavourite) {
                true -> binding.currencyFavouriteImageView.setImageResource(R.drawable.ic_favourite_true)
                false -> binding.currencyFavouriteImageView.setImageResource(R.drawable.ic_favourite_false)
            }

            setFavouriteOnClickListener(item)
        }

        private fun setFavouriteOnClickListener(item: ExchangeRatesModel) {
            binding.currencyFavouriteImageView.setOnClickListener {
                item.isFavourite = !item.isFavourite
                onClickListener.favouriteOnClickListener(item)
                notifyItemChanged(currentList.indexOf(item))
            }
        }
    }
}

interface OnClickListenerFavourites {
    fun favouriteOnClickListener(item: ExchangeRatesModel)
}
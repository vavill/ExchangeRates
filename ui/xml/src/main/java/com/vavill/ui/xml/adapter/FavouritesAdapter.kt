package com.vavill.ui.xml.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vavill.ui.xml.R
import com.vavill.ui.xml.adapter.CurrenciesAdapter.ExchangeRatesDiffCallback
import com.vavill.ui.xml.databinding.ItemCurrencyBinding
import java.util.Locale

class FavouritesAdapter(
    private val onClickListener: OnClickListenerFavourites
) : ListAdapter<com.vavill.domain.model.ExchangeRatesModel, FavouritesAdapter.FavouritesViewHolder>(ExchangeRatesDiffCallback()) {

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

        fun bind(item: com.vavill.domain.model.ExchangeRatesModel) {
            binding.currencyNameTextView.text = item.currencyName
            binding.currencyFullNameTextView.text = item.currencyFullName
            binding.currencyValueTextView.text = String.format(
                Locale.US,
                "%.3f",
                item.currencyValue)

            when (item.isFavourite) {
                true -> binding.currencyFavouriteImageView.setImageResource(
                    com.vavill.common.R.drawable.ic_favourite_true
                )
                false -> binding.currencyFavouriteImageView.setImageResource(
                    com.vavill.common.R.drawable.ic_favourite_false
                )
            }

            setFavouriteOnClickListener(item)
        }

        private fun setFavouriteOnClickListener(item: com.vavill.domain.model.ExchangeRatesModel) {
            binding.currencyFavouriteImageView.setOnClickListener {
                item.isFavourite = !item.isFavourite
                onClickListener.favouriteOnClickListener(item)
                notifyItemChanged(currentList.indexOf(item))
            }
        }
    }
}

interface OnClickListenerFavourites {
    fun favouriteOnClickListener(item: com.vavill.domain.model.ExchangeRatesModel)
}
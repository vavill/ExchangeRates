package com.vavill.exchangerates.ui.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.vavill.exchangerates.R
import com.vavill.exchangerates.ui.fragment.CurrenciesFragment
import com.vavill.exchangerates.ui.fragment.ExchangeRatesFragment
import com.vavill.exchangerates.ui.fragment.FavouritesFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CurrenciesFragment.newInstance()
            1 -> FavouritesFragment.newInstance()
            else -> throw IllegalStateException("Wrong position: $position")
        }
    }
}
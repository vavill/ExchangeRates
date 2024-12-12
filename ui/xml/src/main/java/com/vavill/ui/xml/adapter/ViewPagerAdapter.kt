package com.vavill.ui.xml.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vavill.ui.xml.fragment.CurrenciesFragment
import com.vavill.ui.xml.fragment.FavouritesFragment

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
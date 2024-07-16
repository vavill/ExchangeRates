package com.vavill.exchangerates.ui.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.vavill.exchangerates.R
import com.vavill.exchangerates.collectWithLifecycle
import com.vavill.exchangerates.databinding.FragmentExchangeRatesBinding
import com.vavill.exchangerates.ui.adapter.ViewPagerAdapter
import com.vavill.exchangerates.ui.viewmodel.ExchangeRatesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExchangeRatesFragment : Fragment() {

    companion object {
        fun newInstance() = ExchangeRatesFragment()
    }

    private var _binding: FragmentExchangeRatesBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: ExchangeRatesViewModel by viewModels()
    private lateinit var currentSortType: SortType

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExchangeRatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun init() {
        lifecycleScope.launch {
            viewModel.getBaseCurrencyFromDataStore(requireContext())
        }

        currentSortType = viewModel.currentSortTypeStateFlow.value

        viewModel.baseCurrencyStateFlow.filterNotNull().collectWithLifecycle(viewLifecycleOwner) {
            viewModel.loadExchangeRatesFromApi()
        }

        viewModel.exchangeRatesStateFlow.filterNotNull().collectWithLifecycle(viewLifecycleOwner) {
            setBaseCurrencyUI(viewModel.baseCurrencyStateFlow.value!!)
            viewModel.getFavouriteCurrencies()
        }

        setOnClickListeners()
        viewPagerInit()
        bottomNavigationViewInit()
        searchInit()
    }

    private fun searchInit() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setFilterSearch(newText ?: "")
                return false
            }
        })
    }

    private fun bottomNavigationViewInit() {
        binding.navBottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home_item -> binding.viewPager.currentItem = 0
                R.id.nav_fav_item -> binding.viewPager.currentItem = 1
            }
            true
        }
    }

    private fun viewPagerInit() {
        val adapterVP = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapterVP

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.navBottomNavigationView.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun setOnClickListeners() {
        currentCurrencyCardViewOnClick()
        sortButtonOnClick()
    }

    private fun sortButtonOnClick() {
        binding.sortButtonCardView.setOnClickListener {

            val inflater = LayoutInflater.from(requireContext())
            val popupSortView: View = inflater.inflate(R.layout.popup_sort, null)

            val popupWindow = PopupWindow(
                popupSortView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isFocusable = true

            val sortByAlphabet = popupSortView.findViewById<CardView>(R.id.sortAlphaCardView)
            val sortByValue = popupSortView.findViewById<CardView>(R.id.sortNumericCardView)

            sortByAlphabetOnClick(sortByAlphabet)
            sortByValueOnClick(sortByValue)

            if (currentSortType.toString().contains("ALPHA")) {
                setHighlightForCurrentSortType(sortByAlphabet)
                changeSortIcon(sortByAlphabet, currentSortType)
            } else {
                setHighlightForCurrentSortType(sortByValue)
                changeSortIcon(sortByValue, currentSortType)
            }

            if (popupWindow.isShowing)
                popupWindow.dismiss()
            else
                popupWindow.showAsDropDown(binding.sortButtonCardView)
        }
    }

    private fun changeSortIcon(sortView: CardView, sortType: SortType) {
        val imageView = sortView.children.first() as ImageView
        when (sortType) {
            SortType.ALPHA_ASC -> imageView.setImageResource(R.drawable.ic_sort_alpha_asc)
            SortType.ALPHA_DESC -> imageView.setImageResource(R.drawable.ic_sort_alpha_desc)
            SortType.NUMERIC_ASC -> imageView.setImageResource(R.drawable.ic_sort_numeric_asc)
            SortType.NUMERIC_DESC -> imageView.setImageResource(R.drawable.ic_sort_numeric_desc)
        }
    }

    private fun sortByAlphabetOnClick(sortByAlphabet: CardView) {
        sortByAlphabet.setOnClickListener {
            currentSortType = when (currentSortType) {
                SortType.ALPHA_ASC -> SortType.ALPHA_DESC
                SortType.ALPHA_DESC -> SortType.ALPHA_ASC
                SortType.NUMERIC_ASC -> SortType.ALPHA_ASC
                SortType.NUMERIC_DESC -> SortType.ALPHA_ASC
            }
            viewModel.setCurrentSortType(currentSortType)
            changeSortIcon(sortByAlphabet, currentSortType)
            setHighlightForCurrentSortType(sortByAlphabet)
        }
    }

    private fun sortByValueOnClick(sortByValue: CardView) {
        sortByValue.setOnClickListener {
            currentSortType = when (currentSortType) {
                SortType.ALPHA_ASC -> SortType.NUMERIC_ASC
                SortType.ALPHA_DESC -> SortType.NUMERIC_ASC
                SortType.NUMERIC_ASC -> SortType.NUMERIC_DESC
                SortType.NUMERIC_DESC -> SortType.NUMERIC_ASC
            }
            viewModel.setCurrentSortType(currentSortType)
            changeSortIcon(sortByValue, currentSortType)
            setHighlightForCurrentSortType(sortByValue)
        }
    }

    private fun setHighlightForCurrentSortType(sortView: CardView) {
        (sortView.parent as LinearLayout).children.forEach {
            (it as CardView).setCardBackgroundColor(
                resources.getColor(
                    R.color.white,
                    resources.newTheme()
                )
            )
        }

        sortView.setCardBackgroundColor(
            resources.getColor(
                R.color.my_light_primary,
                resources.newTheme()
            )
        )
    }

    private fun currentCurrencyCardViewOnClick() {
        var isArrowNormal = true

        binding.currentCurrencyCardView.setOnClickListener {
            val scaleYAnimator = if (isArrowNormal)
                ObjectAnimator.ofFloat(
                    binding.currentCurrencyArrowImageView,
                    "scaleY",
                    1f, -1f
                )
            else
                ObjectAnimator.ofFloat(
                    binding.currentCurrencyArrowImageView,
                    "scaleY",
                    -1f, 1f
                )

            scaleYAnimator.duration = 100
            scaleYAnimator.start()

            isArrowNormal = !isArrowNormal

            if (isArrowNormal) {
                binding.searchView.visibility = View.GONE
                binding.searchView.setQuery("", false)
            }
            else
                binding.searchView.visibility = View.VISIBLE
        }
    }

    private fun setBaseCurrencyUI(baseCurrency: String) {
        if (!viewModel.exchangeRatesStateFlow.value.isNullOrEmpty()) {
            val baseCurrencyModel =
                viewModel.exchangeRatesStateFlow.value!!.find { it.currencyName == baseCurrency }

            binding.currentCurrencyNameTextView.text = baseCurrencyModel!!.currencyName
            binding.currentCurrencyFullNameTextView.text = baseCurrencyModel.currencyFullName

            lifecycleScope.launch {
                viewModel.saveBaseCurrencyToDataStore(requireContext(), baseCurrency)
            }
        }
    }
}
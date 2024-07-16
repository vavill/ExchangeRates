package com.vavill.exchangerates.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.vavill.exchangerates.collectWithLifecycle
import com.vavill.exchangerates.databinding.FragmentCurrenciesBinding
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import com.vavill.exchangerates.ui.adapter.CurrenciesAdapter
import com.vavill.exchangerates.ui.adapter.OnClickListenerCurrencies
import com.vavill.exchangerates.ui.viewmodel.ExchangeRatesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull

@AndroidEntryPoint
class CurrenciesFragment : Fragment(), OnClickListenerCurrencies {

    companion object {
        fun newInstance() = CurrenciesFragment()
    }

    private var _binding: FragmentCurrenciesBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: ExchangeRatesViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private lateinit var adapterRV: CurrenciesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrenciesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun init() {
        adapterRV = CurrenciesAdapter(this)
        binding.currencyRecyclerView.adapter = adapterRV

        viewModel.exchangeRatesStateFlow.filterNotNull().collectWithLifecycle(viewLifecycleOwner) {
            Log.d("myTag", "curr fragment start collect")
            adapterRV.submitList(it)
     //       adapterRV.sortData(viewModel.currentSortTypeStateFlow.value)
            Log.d("myTag", "curr has been collected")
        }

        viewModel.currentSortTypeStateFlow.filterNotNull().collectWithLifecycle(viewLifecycleOwner) {
            adapterRV.sortData(it)
            binding.currencyRecyclerView.layoutManager!!.smoothScrollToPosition(
                binding.currencyRecyclerView,
                RecyclerView.State(),
                0
            )
        }

        viewModel.filteredSearchDataStateFlow.filterNotNull().collectWithLifecycle(viewLifecycleOwner) {
            adapterRV.submitList(it)
        }

        swipeRefreshLayoutInit()

        binding.currencyRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                recyclerView.isEnabled = !recyclerView.canScrollVertically(-1)
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun swipeRefreshLayoutInit() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadExchangeRatesFromApi()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun favouriteOnClickListener(item: ExchangeRatesModel) {
        viewModel.insertCurrencyIntoDb(item)
    }

    override fun itemOnClickListener(item: ExchangeRatesModel) {
        viewModel.setBaseCurrency(item.currencyName)
    }
}

enum class SortType {
    ALPHA_ASC,
    ALPHA_DESC,
    NUMERIC_ASC,
    NUMERIC_DESC
}
package com.vavill.exchangerates.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.vavill.exchangerates.databinding.FragmentCurrenciesBinding
import com.vavill.exchangerates.ui.adapter.CurrenciesAdapter
import com.vavill.exchangerates.ui.adapter.GetImageLoader
import com.vavill.exchangerates.ui.viewmodel.ExchangeRatesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrenciesFragment : Fragment(), GetImageLoader {

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
        adapterRV = CurrenciesAdapter(this, viewModel)
        binding.currencyRecyclerView.adapter = adapterRV

        viewModel.exchangeRatesLiveData.observe(viewLifecycleOwner) {
            adapterRV.setData(it)
        }

        viewModel.currentSortTypeLiveData.observe(viewLifecycleOwner) {
            adapterRV.sortData(it)
        }

        viewModel.querySearchLiveData.observe(viewLifecycleOwner) {
            adapterRV.filterData(it)
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
            adapterRV.notifyDataSetChanged()
        }
    }

    override fun loadImageFromUrl(): ImageLoader {
        val imageLoader = ImageLoader.Builder(requireContext()).components {
            add(SvgDecoder.Factory())
        }.build()
        return imageLoader
        //viewModel.setCurrencyImage(currencyName, imageView, imageLoader)
    }
}

enum class SortType {
    ALPHA_ASC,
    ALPHA_DESC,
    NUMERIC_ASC,
    NUMERIC_DESC
}
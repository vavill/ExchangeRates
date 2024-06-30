package com.vavill.exchangerates.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vavill.exchangerates.databinding.FragmentFavouritesBinding
import com.vavill.exchangerates.ui.adapter.FavouritesAdapter
import com.vavill.exchangerates.ui.viewmodel.ExchangeRatesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavouritesFragment()
    }

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: ExchangeRatesViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private lateinit var adapterRV: FavouritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
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
        adapterRV = FavouritesAdapter(viewModel)
        binding.favouritesRecyclerView.adapter = adapterRV
        viewModel.getFavouriteCurrencies()

        viewModel.favouriteCurrenciesLiveData.observe(viewLifecycleOwner) {
            adapterRV.setData(it)
            if (adapterRV.itemCount == 0)
                binding.thereIsNoFavouritesTextView.visibility = View.VISIBLE
            else
                binding.thereIsNoFavouritesTextView.visibility = View.GONE
        }

        swipeRefreshLayoutInit()
    }

    private fun swipeRefreshLayoutInit() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getFavouriteCurrencies()
            viewModel.favouriteCurrenciesLiveData.value?.let {
                adapterRV.setData(it)
            }
            binding.swipeRefreshLayout.isRefreshing = false
            adapterRV.notifyDataSetChanged()
        }
    }
}
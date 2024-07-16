package com.vavill.exchangerates.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vavill.exchangerates.collectWithLifecycle
import com.vavill.exchangerates.databinding.FragmentFavouritesBinding
import com.vavill.exchangerates.domain.model.ExchangeRatesModel
import com.vavill.exchangerates.ui.adapter.FavouritesAdapter
import com.vavill.exchangerates.ui.adapter.OnClickListenerFavourites
import com.vavill.exchangerates.ui.viewmodel.ExchangeRatesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavouritesFragment : Fragment(), OnClickListenerFavourites {

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
        adapterRV = FavouritesAdapter(this)
        binding.favouritesRecyclerView.adapter = adapterRV
        viewModel.getFavouriteCurrencies()

        viewModel.favouriteCurrenciesStateFlow.filterNotNull().collectWithLifecycle(viewLifecycleOwner) {
            adapterRV.submitList(it)
            if (it.isEmpty())
                binding.thereIsNoFavouritesTextView.visibility = View.VISIBLE
            else
                binding.thereIsNoFavouritesTextView.visibility = View.GONE
        }

        swipeRefreshLayoutInit()
    }

    private fun swipeRefreshLayoutInit() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getFavouriteCurrencies()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun favouriteOnClickListener(item: ExchangeRatesModel) {
        viewModel.insertCurrencyIntoDb(item)
    }
}
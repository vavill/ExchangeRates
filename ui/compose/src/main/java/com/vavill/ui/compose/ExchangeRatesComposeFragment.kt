package com.vavill.ui.compose

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.hilt.navigation.compose.hiltViewModel
import com.vavill.common.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vavill.common.viewmodels.ExchangeRatesViewModel
import com.vavill.common.viewmodels.SortType
import com.vavill.domain.model.ExchangeRatesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    val viewModel: ExchangeRatesViewModel = hiltViewModel()
    val context = LocalContext.current

    val baseCurrency by viewModel.baseCurrencyStateFlow.collectAsStateWithLifecycle()
    var baseCurrencyFullName by remember { mutableStateOf("") }

    val currenciesList = remember { mutableStateListOf<ExchangeRatesModel>() }
    val favouritesList = remember { mutableStateListOf<ExchangeRatesModel>() }

    LaunchedEffect(Unit) {
        viewModel.getBaseCurrencyFromDataStore(context)

        launch {
            viewModel.baseCurrencyStateFlow.filterNotNull().first {
                viewModel.loadExchangeRatesFromApi()
                true
            }
        }

        launch {
            viewModel.filteredSearchDataStateFlow.filterNotNull().collect {
                currenciesList.clear()
                currenciesList.addAll(it)
            }
        }

        launch {
            viewModel.exchangeRatesStateFlow.filterNotNull().collect {
                currenciesList.clear()
                currenciesList.addAll(it)
                viewModel.getFavouriteCurrencies()
            }
        }

        launch {
            viewModel.favouriteCurrenciesStateFlow.filterNotNull().collect {
                favouritesList.clear()
                favouritesList.addAll(it)
            }
        }
    }

    LaunchedEffect(baseCurrency) {
        viewModel.exchangeRatesStateFlow.filterNotNull().first { it.isNotEmpty() }
            .find { it.currencyName == baseCurrency }?.let {
                baseCurrencyFullName = it.currencyFullName
                viewModel.saveBaseCurrencyToDataStore(context, it.currencyName)
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            CurrentRate(baseCurrency ?: "", baseCurrencyFullName)

            HorizontalDivider(Modifier.padding(horizontal = 40.dp))

            val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
            HorizontalPager(state = pagerState) { page ->
                val currencies = when (page) {
                    0 -> currenciesList
                    1 -> favouritesList
                    else -> emptyList()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                ) {
                    if (currencies.isEmpty())
                        CircularProgress(Modifier.align(Alignment.Center))
                    else
                        LazyColumn {
                            items(currencies.size) {
                                CurrencyItem(
                                    currencies[it].currencyName,
                                    currencies[it].currencyFullName,
                                    currencies[it].currencyValue,
                                    currencies[it].isFavourite,
                                    viewModel,
                                )
                            }
                        }
                }
            }
            BottomNavBar(pagerState)
        }
    }
}

@Composable
fun SortPopup(
    onDismissRequest: () -> Unit,
    viewModel: ExchangeRatesViewModel
) {
    val sortTypeImages = mutableMapOf(
        Pair(SortType.ALPHA_ASC, R.drawable.ic_sort_alpha_asc),
        Pair(SortType.ALPHA_DESC, R.drawable.ic_sort_alpha_desc),
        Pair(SortType.NUMERIC_ASC, R.drawable.ic_sort_numeric_asc),
        Pair(SortType.NUMERIC_DESC, R.drawable.ic_sort_numeric_desc),
    )

    var sortAlphaImage by remember { mutableStateOf(sortTypeImages[SortType.ALPHA_ASC]) }
    var sortNumericImage by remember { mutableStateOf(sortTypeImages[SortType.NUMERIC_ASC]) }

    var sortAlphaBackgroundColor by remember { mutableIntStateOf(R.color.sort_highlight) }
    var sortNumericBackgroundColor by remember { mutableIntStateOf(R.color.white) }

    Popup(
        offset = IntOffset(-20, 100),
        onDismissRequest = onDismissRequest,
    ) {
        Row(
            Modifier.offset(x = (-10).dp)
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(
                        sortAlphaBackgroundColor
                    )
                ),
                modifier = Modifier
                    .background(colorResource(R.color.my_light_primary), CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)

            ) {
                Image(
                    painterResource(sortAlphaImage!!),
                    contentDescription = "sortAlpha",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(10.dp)
                        .clickable {
                            sortAlphaBackgroundColor = R.color.sort_highlight
                            sortNumericBackgroundColor = R.color.white

                            when (viewModel.currentSortTypeStateFlow.value) {
                                SortType.ALPHA_ASC -> {
                                    viewModel.setSortType(SortType.ALPHA_DESC)
                                    sortAlphaImage = sortTypeImages[SortType.ALPHA_DESC]
                                }

                                SortType.ALPHA_DESC -> {
                                    viewModel.setSortType(SortType.ALPHA_ASC)
                                    sortAlphaImage = sortTypeImages[SortType.ALPHA_ASC]
                                }

                                else -> {
                                    viewModel.setSortType(SortType.ALPHA_ASC)
                                    sortNumericImage = sortTypeImages[SortType.NUMERIC_ASC]
                                }
                            }
                        }
                )
            }

            VerticalDivider(
                thickness = 5.dp,
                modifier = Modifier
                    .height(0.dp)
            )

            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(
                        sortNumericBackgroundColor
                    )
                ),
                modifier = Modifier
                    .border(1.dp, Color.Gray, CircleShape)

            ) {
                Image(
                    painterResource(sortNumericImage!!),
                    contentDescription = "sortAlpha",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(10.dp)
                        .clickable {
                            sortNumericBackgroundColor = R.color.sort_highlight
                            sortAlphaBackgroundColor = R.color.white

                            when (viewModel.currentSortTypeStateFlow.value) {
                                SortType.NUMERIC_ASC -> {
                                    viewModel.setSortType(SortType.NUMERIC_DESC)
                                    sortNumericImage = sortTypeImages[SortType.NUMERIC_DESC]
                                }

                                SortType.NUMERIC_DESC -> {
                                    viewModel.setSortType(SortType.NUMERIC_ASC)
                                    sortNumericImage = sortTypeImages[SortType.NUMERIC_ASC]
                                }

                                else -> {
                                    viewModel.setSortType(SortType.NUMERIC_ASC)
                                    sortAlphaImage = sortTypeImages[SortType.ALPHA_ASC]
                                }
                            }
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomNavBar(
    pagerState: PagerState
) {
    var isHomeSelected by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    NavigationBar(
        containerColor = Color.Transparent,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            NavigationBarItem(
                selected = pagerState.currentPage == 0,
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
                icon = {
                    Icon(
                        painterResource(
                            if (pagerState.currentPage == 0)
                                R.drawable.ic_home_focused
                            else
                                R.drawable.ic_home_unfocused
                        ),
                        contentDescription = "home",
                        modifier = Modifier.size(30.dp)
                    )
                },
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                    isHomeSelected = true
                }
            )

            NavigationBarItem(
                selected = pagerState.currentPage == 1,
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
                icon = {
                    Icon(
                        painterResource(
                            if (pagerState.currentPage == 1)
                                R.drawable.ic_favourite_focused
                            else
                                R.drawable.ic_favourite_unfocused
                        ),
                        contentDescription = "fav",
                        modifier = Modifier.size(30.dp)
                    )
                },
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                    isHomeSelected = false
                }
            )
        }
    }
}

@Composable
fun CurrencyItem(
    name: String,
    fullName: String,
    value: Double,
    isFav: Boolean,
    viewModel: ExchangeRatesViewModel
) {
    val scope = rememberCoroutineScope()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    viewModel.setBaseCurrency(name)
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 10.dp, horizontal = 20.dp)
        ) {
            Column {
                Text(text = name, fontWeight = FontWeight.Bold)
                Text(text = fullName)
            }
            Text(text = value.toString(), modifier = Modifier)
        }
        Image(
            painter = painterResource(
                if (isFav) R.drawable.ic_favourite_true else R.drawable.ic_favourite_false
            ),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp)
                .clickable {
                    viewModel.updateCurrency(
                        ExchangeRatesModel(
                            name,
                            value,
                            fullName,
                            !isFav
                        )
                    )
                    viewModel.getFavouriteCurrencies()
                }
        )
    }

}

@Composable
fun CurrentRate(
    name: String,
    fullName: String
) {
    val viewModel: ExchangeRatesViewModel = hiltViewModel()
    var isPopupVisible by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier
                    .padding(30.dp, 20.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(15.dp))
                    .padding(5.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(20.dp, 5.dp)
                ) {
                    Column {
                        Text(text = name)
                        Text(text = fullName, style = TextStyle())
                    }
                    Image(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(20.dp)
                            .clickable {
                                isSearchExpanded = isSearchExpanded.not()
                                viewModel.setFilterSearch("")
                            }
                    )
                }
            }
            Box {
                Image(
                    painter = painterResource(R.drawable.ic_sort),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 50.dp)
                        .size(30.dp)
                        .clickable {
                            isPopupVisible = true
                        }
                )
                if (isPopupVisible)
                    SortPopup(
                        viewModel = viewModel,
                        onDismissRequest = { isPopupVisible = false }
                    )
            }
        }
        if (isSearchExpanded)
            Search()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun Search() {
    var query by rememberSaveable { mutableStateOf("") }
    val viewModel: ExchangeRatesViewModel = hiltViewModel()

    SearchBar(
        query = query,
        onQueryChange = {
            query = it
            viewModel.setFilterSearch(query)
        },
        onSearch = {},
        active = true,
        onActiveChange = { },
        placeholder = { Text(text = "type currency name...") },
        modifier = Modifier
            .height(70.dp)
            .padding(0.dp)
    ) {

    }

}

@Composable
fun CircularProgress(modifier: Modifier) {
    var delayEnded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(5000L)
        delayEnded = true
    }
    if (delayEnded)
        Text("something went wrong..", modifier = modifier)
    else
        CircularProgressIndicator(modifier = modifier)
}
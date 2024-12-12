package com.vavill.exchangerates.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.vavill.exchangerates.R
import com.vavill.ui.compose.MainScreen
import com.vavill.ui.xml.fragment.ExchangeRatesFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (getLayoutType() == "XML")
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.container, ExchangeRatesFragment.newInstance())
                commit()
            }
        else
            setContent {
                MainScreen()
            }
    }

    private fun getLayoutType() =
        "XML"
//        "COMPOSE"
}
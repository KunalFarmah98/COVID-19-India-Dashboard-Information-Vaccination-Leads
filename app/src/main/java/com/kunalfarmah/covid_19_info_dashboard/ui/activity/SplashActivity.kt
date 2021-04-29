package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.listener.LatestListener
import com.kunalfarmah.covid_19_info_dashboard.ui.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
@ExperimentalCoroutinesApi

class SplashActivity : AppCompatActivity(), LatestListener  {
    private val dashboardViewModel: DashboardViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val window: Window = window

        window.statusBarColor = ContextCompat.getColor(this, R.color.purple_700)
        dashboardViewModel.fetchLatestData()
        dashboardViewModel.fetchActiveDate(this)
        dashboardViewModel.fetchHistoryData()
        dashboardViewModel.fetchContacts()
    }

    override fun goForward() {
        dashboardViewModel.getLatestData()
        dashboardViewModel.getHistoryData()
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

}
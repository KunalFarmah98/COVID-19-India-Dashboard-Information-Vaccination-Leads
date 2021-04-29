package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
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
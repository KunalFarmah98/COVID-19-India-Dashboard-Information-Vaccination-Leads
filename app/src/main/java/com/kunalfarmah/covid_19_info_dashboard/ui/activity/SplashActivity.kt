package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kunalfarmah.covid_19_info_dashboard.util.AppUtil
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivitySplashBinding
import com.kunalfarmah.covid_19_info_dashboard.listener.LatestListener
import com.kunalfarmah.covid_19_info_dashboard.viewModel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
@ExperimentalCoroutinesApi

class SplashActivity : AppCompatActivity(), LatestListener {
    private val dashboardViewModel: DashboardViewModel by viewModels()
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val window: Window = window

        window.statusBarColor = ContextCompat.getColor(this, R.color.purple_700)

        binding.screen.background = resources.getDrawable(R.color.purple_700)
        binding.mainLayout.visibility = View.VISIBLE
        binding.noNetworkLayout.noNetworkLayout.visibility = View.GONE

        getData()

        binding.noNetworkLayout.retry.setOnClickListener {
            binding.noNetworkLayout.noNetworkLayout.visibility = View.GONE
            binding.progress.visibility = View.VISIBLE
            getData()
        }

    }

    override fun goForward() {
        if (!AppUtil.isNetworkAvailable(this) && dashboardViewModel.latestData.value!!.isEmpty()) {
            setNoNetworkLayout()
            return
        }
        dashboardViewModel.getLatestData(null)
        if (!AppUtil.isNetworkAvailable(this)) {
            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                this.finish()
            }, 1500)
        }
        else {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }

    }

    fun setNoNetworkLayout() {
        Handler().postDelayed({
            binding.progress.visibility = View.GONE
            binding.screen.background = resources.getDrawable(R.color.white)
            binding.mainLayout.visibility = View.GONE
            binding.noNetworkLayout.noNetworkLayout.visibility = View.VISIBLE
        }, 1500)

    }

    fun getData() {
        if (AppUtil.isNetworkAvailable(this)) {
            dashboardViewModel.fetchLatestData()
            dashboardViewModel.fetchActiveData(this)
            dashboardViewModel.fetchHistoryData()
            dashboardViewModel.fetchContacts()
        } else {
            dashboardViewModel.getLatestData(this)
        }
    }

}
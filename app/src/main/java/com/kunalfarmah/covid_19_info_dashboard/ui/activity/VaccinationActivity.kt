package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebChromeClient
import androidx.navigation.*
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivityVaccinationBinding
import com.kunalfarmah.covid_19_info_dashboard.ui.WebViewFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class VaccinationActivity : AppCompatActivity() {

    var sharedPreferences: SharedPreferences? = null
    lateinit var action: String
    val args:VaccinationActivityArgs by navArgs()
    lateinit var binding:ActivityVaccinationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        binding= ActivityVaccinationBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.webChromeClient = WebChromeClient()

        action = intent.getStringExtra("action")!!

        if(action.isNullOrEmpty())
            action = args.action

        if (action == Constants.REGISTRATION) {
            supportActionBar?.title = "Vaccine Registration on COWIN Portal"
            binding.webView.loadUrl("https://selfregistration.cowin.gov.in/")

        } else if (action == Constants.CERTIFICATE) {
            supportActionBar?.title = "Vaccination Certificate"
            binding.webView.loadUrl("https://selfregistration.cowin.gov.in/")

        }
        else if (action == Constants.VACCINE_DASHBAORD){
            supportActionBar?.title = "COWIN Dashboard"
            binding.webView.loadUrl("https://dashboard.cowin.gov.in/")

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if(binding.webView.canGoBack()){
            binding.webView.goBack()
        }
        else
            super.onBackPressed()
    }
}
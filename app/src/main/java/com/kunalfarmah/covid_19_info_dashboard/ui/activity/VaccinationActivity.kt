package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import androidx.navigation.*
import com.kunalfarmah.covid_19_info_dashboard.util.AppUtil
import com.kunalfarmah.covid_19_info_dashboard.util.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivityVaccinationBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class VaccinationActivity : AppCompatActivity() {

    var sharedPreferences: SharedPreferences? = null
    lateinit var action: String
    val args:VaccinationActivityArgs by navArgs()
    lateinit var binding:ActivityVaccinationBinding
    var url:String?=null

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
            supportActionBar?.title = "COWIN Vaccine Registration"
            url = "https://selfregistration.cowin.gov.in/"
            loadUrl(url!!)

        } else if (action == Constants.CERTIFICATE) {
            supportActionBar?.title = "Vaccination Certificate"
            url = "https://selfregistration.cowin.gov.in/"
            loadUrl(url!!)

        }
        else if (action == Constants.VACCINE_DASHBAORD){
            supportActionBar?.title = "COWIN Dashboard"
            url = "https://dashboard.cowin.gov.in/"
            loadUrl(url!!)

        }

        binding.noNetworkLayout.retry.setOnClickListener {
            loadUrl(url!!)
        }

    }

    private fun loadUrl(url:String){
        if(AppUtil.isNetworkAvailable(this)) {
            binding.webView.visibility = View.VISIBLE
            binding.noNetworkLayout.noNetworkLayout.visibility = View.GONE
            binding.webView.loadUrl(url)
        }
        else{
            binding.webView.visibility = View.GONE
            binding.noNetworkLayout.noNetworkLayout.visibility = View.VISIBLE
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId== R.id.nav_home)
            finish()

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
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
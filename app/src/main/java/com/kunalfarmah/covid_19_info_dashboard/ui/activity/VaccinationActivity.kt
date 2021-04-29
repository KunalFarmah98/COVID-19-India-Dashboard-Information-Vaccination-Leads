package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.*
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.ui.WebViewFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class VaccinationActivity : AppCompatActivity() {

    var sharedPreferences: SharedPreferences? = null
    lateinit var action: String
    val args:VaccinationActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vaccination)
        sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE)

        action = args.action
        if (action == Constants.REGISTRATION) {
            supportActionBar?.title = "Vaccine Registration on COWIN Portal"
        } else if (action == Constants.CERTIFICATE) {
            supportActionBar?.title = "Vaccination Certificate"
        }
        var webViewFragment =  WebViewFragment()
        var args = Bundle()
        args.putString("url","https://selfregistration.cowin.gov.in/")
        webViewFragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.fragment,webViewFragment).addToBackStack(WebViewFragment.TAG)
            .commit()



    }
}
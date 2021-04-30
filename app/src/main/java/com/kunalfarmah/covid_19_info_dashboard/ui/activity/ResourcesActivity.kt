package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.kunalfarmah.covid_19_info_dashboard.AppUtil
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivityResourcesBinding


class ResourcesActivity : AppCompatActivity() {
    val args: ResourcesActivityArgs by navArgs()
    var url:String?=null
    lateinit var binding: ActivityResourcesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        binding = ActivityResourcesBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        var action = args.action

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.webChromeClient = WebChromeClient()

        when(action){
            Constants.SPRINKLR_DASHBOARD -> {
                supportActionBar?.title = "Spinklr's dashboard for Covid-19 Leads and Resources"
                url = "https://external.sprinklr.com/insights/explorer/dashboard/601b9e214c7a6b689d76f493/tab/16?id=DASHBOARD_601b9e214c7a6b689d76f493&home=1"
                loadUrl(url!!)
            }
            Constants.INDIA_RESOURCES -> {
                supportActionBar?.title = "Covid-19 India Resources"
                url = "https://www.covidindiaresources.com/"
                binding.webView.loadUrl(url!!)
            }
            Constants.HOSPITAL_MAP -> {
                supportActionBar?.title = "Covid-19 Hospitals on Map"
                val url = "https://covid-19-hospital-data.el.r.appspot.com/"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                binding.webView.visibility = View.GONE
                binding.info.visibility = View.VISIBLE
                binding.url.text =
                    Html.fromHtml("<u>https://covid-19-hospital-data.el.r.appspot.com</u>")
                binding.url.setOnClickListener {
                    startActivity(intent)
                }
                if(isChromeInstalled()) {
                    intent.`package` = "com.android.chrome"
                    startActivity(intent)
                }
                else{
                    startActivity(intent)
                }
            }
            Constants.MH_COVID_HELP_DESK->{
                supportActionBar?.title = "Covid-19 Hospitals on Map"
                val url = "https://docs.google.com/spreadsheets/u/0/d/16-UdbV7hX7vRVxT2EVHDjXscaAnE5QKTjG-7JIFdOHM/htmlview"
                loadUrl(url)
            }
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

        if(item.itemId==R.id.nav_home)
            onBackPressed()

        return super.onOptionsItemSelected(item)
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

    private fun isChromeInstalled(): Boolean {
        var pInfo: PackageInfo?=null
        pInfo = try {
            packageManager.getPackageInfo("com.android.chrome", 0)
        } catch (e: PackageManager.NameNotFoundException) {
            //chrome is not installed on the device
            return false
        }
        return null!=pInfo
    }
}
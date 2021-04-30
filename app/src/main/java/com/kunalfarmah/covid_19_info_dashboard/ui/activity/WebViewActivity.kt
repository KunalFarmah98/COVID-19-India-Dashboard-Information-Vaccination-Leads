package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebChromeClient
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    lateinit var binding:ActivityWebViewBinding
    var action:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        binding = ActivityWebViewBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.webChromeClient = WebChromeClient()

        action = intent.getStringExtra("action")

        when(action){
            "Info"->{
                supportActionBar?.title = "Covid-19 India Information"
                binding.webView.loadUrl("https://www.mohfw.gov.in/")
            }
            "Donate"->{
                supportActionBar?.title = "Donate to PM Cares Fund"
                binding.webView.loadUrl("https://www.pmcares.gov.in/en/web/contribution/donate_india")
            }
            "Privacy"->{
                supportActionBar?.title = "Privacy Policy"
                binding.webView.loadUrl("https://kunal-farmah.jimdosite.com/contact-me/")
            }
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
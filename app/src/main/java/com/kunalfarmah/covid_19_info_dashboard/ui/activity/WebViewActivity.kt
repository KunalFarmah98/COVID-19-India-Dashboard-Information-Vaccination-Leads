package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import com.kunalfarmah.covid_19_info_dashboard.AppUtil
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityWebViewBinding
    var action: String? = null
    var url: String? = null
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

        when (action) {
            "Info" -> {
                supportActionBar?.title = "Covid-19 India Information"
                url = "https://www.mohfw.gov.in/"
                loadUrl(url!!)
            }
            "Donate" -> {
                supportActionBar?.title = "Donate to PM Cares Fund"
                url = "https://www.pmcares.gov.in/en/web/contribution/donate_india"
                loadUrl(url!!)
            }
            "Privacy" -> {
                supportActionBar?.title = "Privacy Policy"
                url = "https://kunal-farmah.jimdosite.com/contact-me/"
                loadUrl(url!!)
            }
        }

        binding.noNetworkLayout.retry.setOnClickListener {
            loadUrl(url!!)
        }
    }

    private fun loadUrl(url: String) {
        if (AppUtil.isNetworkAvailable(this)) {
            binding.webView.visibility = View.VISIBLE
            binding.noNetworkLayout.noNetworkLayout.visibility = View.GONE
            binding.webView.loadUrl(url)
        } else {
            binding.webView.visibility = View.GONE
            binding.noNetworkLayout.noNetworkLayout.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else
            super.onBackPressed()
    }
}
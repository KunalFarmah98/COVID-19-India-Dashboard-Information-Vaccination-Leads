package com.kunalfarmah.covid_19_info_dashboard.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.kunalfarmah.covid_19_info_dashboard.R


class WebViewFragment() : Fragment() {


    companion object{
        val TAG = "WebViewFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view = inflater.inflate(R.layout.fragment_web_view, container, false)

        view.findViewById<WebView>(R.id.webView).loadUrl(requireArguments().getString("url",""))
        return view
    }

}
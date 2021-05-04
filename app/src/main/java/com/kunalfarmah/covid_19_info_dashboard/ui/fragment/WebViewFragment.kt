package com.kunalfarmah.covid_19_info_dashboard.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kunalfarmah.covid_19_info_dashboard.util.AppUtil
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentWebViewBinding


class WebViewFragment : Fragment() {


    lateinit var binding: FragmentWebViewBinding
    var url:String?=null
    companion object{
        val TAG = "WebViewFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentWebViewBinding.inflate(inflater)

        url = requireArguments().getString("url","")
        loadUrl(url!!)
        binding.noNetworkLayout.retry.setOnClickListener {
            loadUrl(url!!)
        }
        return binding.root

    }

    private fun loadUrl(url: String) {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            binding.webView.visibility = View.VISIBLE
            binding.noNetworkLayout.noNetworkLayout.visibility = View.GONE
            binding.webView.loadUrl(url)
        } else {
            binding.webView.visibility = View.GONE
            binding.noNetworkLayout.noNetworkLayout.visibility = View.VISIBLE
        }
    }
}
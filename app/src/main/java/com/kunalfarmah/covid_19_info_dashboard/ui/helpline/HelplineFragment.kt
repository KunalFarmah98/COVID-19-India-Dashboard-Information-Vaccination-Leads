package com.kunalfarmah.covid_19_info_dashboard.ui.helpline

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentHelplineBinding
import com.kunalfarmah.covid_19_info_dashboard.retrofit.ContactsRegionalItem
import com.kunalfarmah.covid_19_info_dashboard.ui.WebViewFragment
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.HelplineAdapter
import com.kunalfarmah.covid_19_info_dashboard.ui.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class HelplineFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    lateinit var binding: FragmentHelplineBinding
    private var sPref: SharedPreferences? = null
    var list: List<ContactsRegionalItem?>? = null
    var mAdapter: HelplineAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHelplineBinding.inflate(inflater)
        sPref = activity?.getSharedPreferences(Constants.PREFS, MODE_PRIVATE)

        list = dashboardViewModel.contactsData.value?.contacts?.regional

        if (!list.isNullOrEmpty()) {
            setView(list!!)
        }

        dashboardViewModel.getContacts()

        dashboardViewModel.contactsData.observe(viewLifecycleOwner, {
            if (null != it) {
                list = it.contacts?.regional
                setView(list!!)
            }
        })

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var tempAdapter: HelplineAdapter? = null
            var temp: List<ContactsRegionalItem>? = null
            override fun onQueryTextSubmit(query: String): Boolean {
                temp = ArrayList<ContactsRegionalItem>()
                for (case in list!!) {
                    if (case!!.loc?.contains(query, true)!!)
                        (temp as ArrayList<ContactsRegionalItem>).add(case)
                }
                tempAdapter = HelplineAdapter(context, temp as ArrayList<ContactsRegionalItem>)
                binding.helplines.adapter = tempAdapter
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                temp = ArrayList<ContactsRegionalItem>()
                for (case in list!!) {
                    if (case!!.loc?.contains(newText, true) == true)
                        (temp as ArrayList<ContactsRegionalItem>).add(case)
                }
                tempAdapter = HelplineAdapter(context, temp as ArrayList<ContactsRegionalItem>)
                binding.helplines.adapter = tempAdapter
                return true
            }
        })

        binding.website.setOnClickListener(View.OnClickListener {
            var webViewFragment =  WebViewFragment()
            var args = Bundle()
            args.putString("url","https://www.mohfw.gov.in/")
            webViewFragment.arguments = args
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment,webViewFragment)?.addToBackStack(WebViewFragment.TAG)
                ?.commit()
        })


        binding.helpline.setOnClickListener(View.OnClickListener {
            val call = Intent(Intent.ACTION_DIAL)
            call.data = Uri.parse("tel:" + binding.helpline.text.toString())
            startActivity(call)
        })

        binding.helpline2.setOnClickListener(View.OnClickListener {
            val call = Intent(Intent.ACTION_DIAL)
            call.data = Uri.parse("tel:" + binding.helpline2.text.toString())
            startActivity(call)
        })

        return binding.root
    }

    private fun setView(list: List<ContactsRegionalItem?>) {
        binding.helplines.layoutManager = LinearLayoutManager(activity)
        binding.helplines.setHasFixedSize(true)
        mAdapter = HelplineAdapter(activity, list)
        binding.helplines.adapter = mAdapter
    }


}
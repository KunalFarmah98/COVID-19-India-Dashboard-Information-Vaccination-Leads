package com.kunalfarmah.covid_19_info_dashboard.ui.dashboard

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentHomeBinding
import com.kunalfarmah.covid_19_info_dashboard.retrofit.Summary
import com.kunalfarmah.covid_19_info_dashboard.room.CovidEntity
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.DashboardAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var mAdapter: DashboardAdapter? = null
    private lateinit var binding: FragmentHomeBinding
    private var sPref: SharedPreferences? = null
    private var list: List<CovidEntity>? = null

    companion object{
        val TAG = "Dashboard Fragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.actionBar?.title = "Dashboard"
        binding = FragmentHomeBinding.inflate(inflater)
        sPref = activity?.getSharedPreferences(Constants.PREFS, MODE_PRIVATE)

        val summary = Gson().fromJson(
            sPref?.getString(Constants.LATEST_SUMMARY, ""),
            Summary::class.java
        )
        if (null != summary) {
            val df = DecimalFormat("##,##,###");
            binding.totalSummary.text = String.format(
                "Total Cases: %s",
                df.format(Integer.parseInt(summary.total.toString()))
            )
            binding.recoveredSummary.text =
                String.format(
                    "Recovered:\n%s",
                    df.format(Integer.parseInt(summary.discharged.toString()))
                )
            binding.deceasedSummary.text = String.format(
                "Deceased:\n%s",
                df.format(Integer.parseInt(summary.deaths.toString()))
            )
            binding.activeSummary.text = String.format(
                "Active:\n%s",
                df.format(Integer.parseInt(sPref?.getString(Constants.LATEST_ACTIVE, "0")))
            )
        }
        dashboardViewModel.getLatestData()
        dashboardViewModel.getHistoryData()

        binding.latestRecycler.layoutManager = LinearLayoutManager(context)
        binding.latestRecycler.setHasFixedSize(true)

        dashboardViewModel.latestData.observe(viewLifecycleOwner, {
            Log.d("latest", it.toString())
            list = it
            mAdapter = DashboardAdapter(context, it)
            binding.latestRecycler.adapter = mAdapter
        })

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var tempAdapter: DashboardAdapter? = null
            var temp: List<CovidEntity>?=null
            override fun onQueryTextSubmit(query: String): Boolean {
                temp = ArrayList<CovidEntity>()
                for (case in list!!) {
                    if (case.state.contains(query, true))
                        (temp as ArrayList<CovidEntity>).add(case)
                }
                tempAdapter = DashboardAdapter(context, temp as ArrayList<CovidEntity>)
                binding.latestRecycler.adapter = tempAdapter
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                temp = ArrayList<CovidEntity>()
                for (case in list!!) {
                    if (case.state.contains(newText, true))
                        (temp as ArrayList<CovidEntity>).add(case)
                }
                tempAdapter = DashboardAdapter(context, temp as ArrayList<CovidEntity>)
                binding.latestRecycler.adapter = tempAdapter
                return true
            }
        })

        return binding.root
    }

    fun find(list: List<CovidEntity>?, query: String): Boolean {

        return false
    }
}
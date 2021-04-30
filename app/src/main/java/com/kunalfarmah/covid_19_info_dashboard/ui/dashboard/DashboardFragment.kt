package com.kunalfarmah.covid_19_info_dashboard.ui.dashboard

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.Gson
import com.kunalfarmah.covid_19_info_dashboard.AppUtil
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentDashboardBinding
import com.kunalfarmah.covid_19_info_dashboard.retrofit.Summary
import com.kunalfarmah.covid_19_info_dashboard.room.CovidEntity
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.HelplineActivity
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.VaccinationActivity
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.DashboardAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class DashboardFragment : Fragment(), OnChartValueSelectedListener {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var mAdapter: DashboardAdapter? = null
    private lateinit var binding: FragmentDashboardBinding
    private var sPref: SharedPreferences? = null
    private var list: List<CovidEntity>? = null
    private var summary: Summary? = null
    var active: Float? = null
    var recovered: Float? = null
    var deceaased: Float? = null

    companion object {
        val TAG = "Dashboard Fragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.actionBar?.title = "Dashboard"
        dashboardViewModel.getLatestData(null)
        dashboardViewModel.getHistoryData()
        binding = FragmentDashboardBinding.inflate(inflater)
        sPref = activity?.getSharedPreferences(Constants.PREFS, MODE_PRIVATE)

        summary = Gson().fromJson(
            sPref?.getString(Constants.LATEST_SUMMARY, ""),
            Summary::class.java
        )
        if (null != summary) {
            val df = DecimalFormat("##,##,###");
            binding.totalSummary.text = String.format(
                "Total Cases:\n%s",
                df.format(Integer.parseInt(summary?.total.toString()))
            )
            binding.recoveredSummary.text =
                String.format(
                    "Recovered:\n%s",
                    df.format(Integer.parseInt(summary?.discharged.toString()))
                )
            binding.deceasedSummary.text = String.format(
                "Deceased:\n%s",
                df.format(Integer.parseInt(summary?.deaths.toString()))
            )
            binding.activeSummary.text = String.format(
                "Active:\n%s",
                df.format(Integer.parseInt(sPref?.getString(Constants.LATEST_ACTIVE, "0")!!))
            )
        }

        setUpPieChart()

        list = dashboardViewModel.latestData.value
        if (!list.isNullOrEmpty()) {
            setView(list!!)
        } else {
            binding.loading.visibility = View.VISIBLE
            binding.loading.startShimmerAnimation()
        }

        if(AppUtil.isNetworkAvailable(requireContext()))
            dashboardViewModel.fetchActiveData(null)


        binding.latestRecycler.layoutManager = LinearLayoutManager(context)
        binding.latestRecycler.setHasFixedSize(true)

        dashboardViewModel.latestData.observe(viewLifecycleOwner, {
            Log.d("latest", it.toString())
            list = it
            setView(it)
        })

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var tempAdapter: DashboardAdapter? = null
            var temp: List<CovidEntity>? = null
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

        binding.help.setOnClickListener {
            startActivity(Intent(activity, HelplineActivity::class.java))
        }
        binding.getVaccinated.setOnClickListener {
            var intent = Intent(activity, VaccinationActivity::class.java)
            intent.putExtra("action", Constants.REGISTRATION)
            startActivity(intent)
        }
        return binding.root
    }

    private fun setUpPieChart() {
        active = sPref?.getString(Constants.LATEST_ACTIVE, "0")?.toFloat()
        recovered = summary?.discharged?.toFloat()
        deceaased = summary?.deaths?.toFloat()

        var pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(active!!))
        pieEntries.add(PieEntry(recovered!!))
        pieEntries.add(PieEntry(deceaased!!))

        var dataSet = PieDataSet(pieEntries, "Daily Summary")
        var blue: Int = resources.getColor(R.color.blue)
        var red: Int = resources.getColor(R.color.red)
        var green: Int = resources.getColor(R.color.green)
        var colors = ArrayList<Int>()
        colors.add(blue)
        colors.add(green)
        colors.add(red)
        dataSet.colors = colors
        dataSet.isHighlightEnabled = true
        var pieData = PieData(dataSet)
        binding.pieChart.data = pieData
        binding.pieChart.data.setValueTextSize(13f)
        binding.pieChart.data.setValueTextColor(resources.getColor(R.color.white))
        binding.pieChart.centerText = "Rates in % (rounded off)"
        binding.pieChart.setCenterTextSize(15f)
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setOnChartValueSelectedListener(this)
        binding.pieChart.invalidate()
    }

    private fun setView(list: List<CovidEntity>) {
        binding.loading.visibility = View.GONE
        binding.loading.stopShimmerAnimation()
        binding.latestRecycler.visibility = View.VISIBLE
        mAdapter = DashboardAdapter(context, list)
        binding.latestRecycler.adapter = mAdapter
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e!!.equalTo(PieEntry(active!!))){
            binding.activeSummary.textSize = 22f
            binding.deceasedSummary.textSize = 20f
            binding.recoveredSummary.textSize = 20f

        }
        else if (e!!.equalTo(PieEntry(recovered!!))){
            binding.activeSummary.textSize = 20f
            binding.deceasedSummary.textSize = 20f
            binding.recoveredSummary.textSize = 22f
        }
        if (e!!.equalTo(PieEntry(deceaased!!))){
            binding.activeSummary.textSize = 20f
            binding.deceasedSummary.textSize = 22f
            binding.recoveredSummary.textSize = 20f
        }
    }

    override fun onNothingSelected() {
        binding.activeSummary.textSize = 20f
        binding.deceasedSummary.textSize = 20f
        binding.recoveredSummary.textSize = 20f
    }
}
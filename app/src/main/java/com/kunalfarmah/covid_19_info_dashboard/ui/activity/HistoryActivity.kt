package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.kunalfarmah.covid_19_info_dashboard.util.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivityHistoryBinding
import com.kunalfarmah.covid_19_info_dashboard.room.CovidEntity
import com.kunalfarmah.covid_19_info_dashboard.room.CovidHistoryEntity
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.HistoryStateWiseAdapter
import com.kunalfarmah.covid_19_info_dashboard.viewModel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class HistoryActivity : AppCompatActivity(), OnChartValueSelectedListener {
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var binding: ActivityHistoryBinding
    private var sPref:SharedPreferences?=null
    private var date:String?=null
    private lateinit var mAdapter: HistoryStateWiseAdapter
    private var list: List<CovidHistoryEntity>?=null
    private var total:Int?=null
    private var recovered:Int?=null
    private var deceased:Int?=null

    private var dTotal:Int?=null
    private var dRecovered:Int?=null
    private var dDeceased:Int?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        supportActionBar?.title = "History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        sPref = getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
        date = sPref?.getString(Constants.SELECTED_DATE, "")
        dashboardViewModel.getHistoryByDate(date!!)


        list = dashboardViewModel.historyDateData.value

        if(!list.isNullOrEmpty()){
            setView(list!!)
        }
        else{
            binding.layout.visibility = View.GONE
            binding.loading.visibility = View.VISIBLE
            binding.loading.startShimmerAnimation()
        }

        dashboardViewModel.historyDateData.observe(this, {
            if (it != null) {
                list = it
                setView(it)
            }
        })

        setUpPieChart()
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var tempAdapter: HistoryStateWiseAdapter? = null
            var temp: List<CovidEntity>? = null
            override fun onQueryTextSubmit(query: String): Boolean {
                temp = ArrayList<CovidEntity>()
                for (case in list!!) {
                    if (case.state.contains(query, true))
                        (temp as ArrayList<CovidHistoryEntity>).add(case)
                }
                tempAdapter = HistoryStateWiseAdapter(
                    this@HistoryActivity,
                    temp as ArrayList<CovidHistoryEntity>
                )
                binding.historyRecycler.adapter = tempAdapter
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                temp = ArrayList<CovidEntity>()
                for (case in list!!) {
                    if (case.state.contains(newText, true))
                        (temp as ArrayList<CovidHistoryEntity>).add(case)
                }
                tempAdapter = HistoryStateWiseAdapter(
                    this@HistoryActivity,
                    temp as ArrayList<CovidHistoryEntity>
                )
                binding.historyRecycler.adapter = tempAdapter
                return true
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun setView(list: List<CovidHistoryEntity>){
        val df = DecimalFormat("##,##,###")
        val dateFormatter1 =  SimpleDateFormat("yyyy-MM-dd")
        var date_ = dateFormatter1.parse(date)
        var cal = Calendar.getInstance()
        cal.time = date_

        var day = cal.get(Calendar.DAY_OF_MONTH)
        var month = cal.get(Calendar.MONTH)
        var year = cal.get(Calendar.YEAR)

        var month_ = arrayOf("January","February","March","April","May","June","July","August","September","October","November","December")

        var fullMonth = month_[month]

        var fullDate = String.format("%s %s %s",day, fullMonth,year)

        binding.layout.visibility = View.VISIBLE
        binding.layout.scrollTo(0, 0)
        binding.loading.visibility = View.GONE
        binding.loading.stopShimmerAnimation()
        binding.dateSummary.text = fullDate
        total = Integer.parseInt(sPref?.getString(Constants.SELECTED_TOTAL, "")!!)
        recovered = Integer.parseInt(sPref?.getString(Constants.SELECTED_RECOVERED, "")!!)
        deceased = Integer.parseInt(sPref?.getString(Constants.SELECTED_DECEASED, "")!!)
        dTotal = Integer.parseInt(sPref?.getString(Constants.SELECTED_DAILYTOTAL, "")!!)
        dRecovered = Integer.parseInt(sPref?.getString(Constants.SELECTED_DAILYRECOVERED, "")!!)
        dDeceased = Integer.parseInt(sPref?.getString(Constants.SELECTED_DAILYDECEASED, "")!!)
        binding.activeSummary.text = String.format("Total:\n%s", df.format(total))
        binding.recoveredSummary.text = String.format("Recovered:\n%s", df.format(total))
        binding.deceasedSummary.text = String.format("Deceased:\n%s", df.format(deceased))
        binding.dailyNew.text = String.format("Cases:\n%s", df.format(dTotal))
        binding.dailyRecovered.text = String.format("Recoveries:\n%s", df.format(dRecovered))
        binding.dailyDeceased.text = String.format("Deaths:\n%s", df.format(dDeceased))

        if(list.isEmpty()){
            binding.historyRecycler.visibility = View.GONE
            binding.noDataLayout.visibility = View.VISIBLE
        }
        else {
            binding.historyRecycler.layoutManager = LinearLayoutManager(this)
            binding.historyRecycler.setHasFixedSize(true)
            mAdapter = HistoryStateWiseAdapter(this@HistoryActivity, list)
            binding.historyRecycler.adapter = mAdapter
        }
    }

    private fun setUpPieChart() {
        total = Integer.parseInt(sPref?.getString(Constants.SELECTED_TOTAL, "")!!)
        recovered = Integer.parseInt(sPref?.getString(Constants.SELECTED_RECOVERED, "")!!)
        deceased = Integer.parseInt(sPref?.getString(Constants.SELECTED_DECEASED, "")!!)
        var pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(recovered!!.toFloat()))
        pieEntries.add(PieEntry(deceased!!.toFloat()))

        var dataSet = PieDataSet(pieEntries, String.format("Summary for %s", date))
        var red:Int= resources.getColor(R.color.red)
        var green:Int = resources.getColor(R.color.green)
        var colors = ArrayList<Int>()
        colors.add(green)
        colors.add(red)
        dataSet.colors = colors
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

    override fun onValueSelected(e: Entry?, h: Highlight?) {

         if (e!!.equalTo(PieEntry(recovered?.toFloat()!!))) {
            binding.deceasedSummary.textSize = 16f
            binding.recoveredSummary.textSize = 18f
        }
        if (e.equalTo(PieEntry(deceased?.toFloat()!!))) {
            binding.deceasedSummary.textSize = 18f
            binding.recoveredSummary.textSize = 16f
        }
    }

    override fun onNothingSelected() {
        binding.deceasedSummary.textSize = 16f
        binding.recoveredSummary.textSize = 16f
    }

    override fun onResume() {
        binding.layout.scrollTo(0,0)
        super.onResume()
    }
}
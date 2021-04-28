package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivityHistoryBinding
import com.kunalfarmah.covid_19_info_dashboard.room.CovidEntity
import com.kunalfarmah.covid_19_info_dashboard.room.CovidHistoryEntity
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.DashboardAdapter
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.HistoryStateWiseAdapter
import com.kunalfarmah.covid_19_info_dashboard.ui.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat
import java.text.SimpleDateFormat

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class HistoryActivity : AppCompatActivity() {
    private val dashboardViewModel:DashboardViewModel by viewModels()
    private lateinit var binding: ActivityHistoryBinding
    private var sPref:SharedPreferences?=null
    private var date:String?=null
    private lateinit var mAdapter: HistoryStateWiseAdapter
    private var list: List<CovidHistoryEntity>?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        supportActionBar?.title = "History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        sPref = getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
        date = sPref?.getString(Constants.SELECTED_DATE,"")
        dashboardViewModel.getHistoryByDate(date!!)
        val df = DecimalFormat("##,##,###")
        val dateFormatter1: SimpleDateFormat =  SimpleDateFormat("yyyy-MM-dd")
        val dateFormatter2: SimpleDateFormat = SimpleDateFormat("dd/MM/yyy")

        binding.layout.visibility = View.GONE
        binding.loading.visibility = View.VISIBLE
        binding.loading.startShimmerAnimation()

        dashboardViewModel.historyDateData.observe(this,{
            if(it!=null){
                list = it
                binding.layout.visibility = View.VISIBLE
                binding.loading.visibility = View.GONE
                binding.loading.stopShimmerAnimation()
                binding.dateSummary.text = dateFormatter2.format(dateFormatter1.parse(date)!!)
                binding.totalSummary.text = String.format("Total:\n%s",df.format(Integer.parseInt(sPref?.getString(Constants.SELECTED_TOTAL,"")!!)))
                binding.recoveredSummary.text = String.format("Recovered:\n%s",df.format(Integer.parseInt(sPref?.getString(Constants.SELECTED_RECOVERED,"")!!)))
                binding.deceasedSummary.text = String.format("Deceased:\n%s",df.format(Integer.parseInt(sPref?.getString(Constants.SELECTED_DECEASED,"")!!)))

                binding.historyRecycler.layoutManager = LinearLayoutManager(this)
                binding.historyRecycler.setHasFixedSize(true)
                mAdapter = HistoryStateWiseAdapter(this@HistoryActivity, it)
                binding.historyRecycler.adapter = mAdapter
            }
        })

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var tempAdapter: HistoryStateWiseAdapter? = null
            var temp: List<CovidEntity>?=null
            override fun onQueryTextSubmit(query: String): Boolean {
                temp = ArrayList<CovidEntity>()
                for (case in list!!) {
                    if (case.state.contains(query, true))
                        (temp as ArrayList<CovidHistoryEntity>).add(case)
                }
                tempAdapter = HistoryStateWiseAdapter(this@HistoryActivity, temp as ArrayList<CovidHistoryEntity>)
                binding.historyRecycler.adapter = tempAdapter
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                temp = ArrayList<CovidEntity>()
                for (case in list!!) {
                    if (case.state.contains(newText, true))
                        (temp as ArrayList<CovidHistoryEntity>).add(case)
                }
                tempAdapter = HistoryStateWiseAdapter(this@HistoryActivity, temp as ArrayList<CovidHistoryEntity>)
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
}
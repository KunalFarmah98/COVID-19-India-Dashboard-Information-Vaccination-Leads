package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivityStatsBinding
import com.kunalfarmah.covid_19_info_dashboard.util.Constants
import java.text.DecimalFormat

class StatsActivity : AppCompatActivity() {

    lateinit var binding: ActivityStatsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(LayoutInflater.from(this))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        var args = intent.extras
        supportActionBar?.title = args?.getString("State", "State")
        setContentView(binding.root)
        var total = args?.getString("Total")
        var active = args?.getString("Active")
        var recovered = args?.getString("Recovered")
        var deceased = args?.getString("Deceased")
        var dActive = args?.getString("dActive")
        var dRecovered = args?.getString("dRecovered")
        var dDeceased = args?.getString("dDeceased")

        if (dActive == "0" && dRecovered == "0" && dDeceased == "0") {
            binding.recentLayout.visibility = View.GONE
        }

        val df = DecimalFormat("##,##,###")

        binding.total.text = String.format("Total Cases: %s", df.format(total?.toInt()))
        binding.activeSummary.text = String.format("Active:\n%s", df.format(active?.toInt()))
        binding.recoveredSummary.text =
            String.format("Recovered:\n%s", df.format(recovered?.toInt()))
        binding.deceasedSummary.text = String.format("Deceased:\n%s", df.format(deceased?.toInt()))

        setUpTotalPieChart(active?.toFloat()!!, recovered?.toFloat()!!, deceased?.toFloat()!!)


        binding.dailyNew.text = String.format("Cases:\n%s", df.format(dActive?.toInt()))
        binding.dailyRecovered.text =
            String.format("Recoveries:\n%s", df.format(dRecovered?.toInt()))
        binding.dailyDeceased.text = String.format("Deaths:\n%s", df.format(dDeceased?.toInt()))

        setUpRecentPieChart(dActive?.toFloat()!!, dRecovered?.toFloat()!!, dDeceased?.toFloat()!!)

    }

    private fun setUpTotalPieChart(active: Float, recovered: Float, deceased: Float) {

        var pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(active))
        pieEntries.add(PieEntry(recovered))
        pieEntries.add(PieEntry(deceased))

        var dataSet = PieDataSet(pieEntries, "")
        var blue: Int = resources.getColor(R.color.blue)
        var red: Int = resources.getColor(R.color.red)
        var green: Int = resources.getColor(R.color.green)
        var colors = ArrayList<Int>()
        colors.add(blue)
        colors.add(green)
        colors.add(red)
        dataSet.colors = colors
        var pieData = PieData(dataSet)
        binding.pieChartTotal.data = pieData
        binding.pieChartTotal.data.setValueTextSize(13f)
        binding.pieChartTotal.data.setValueTextColor(resources.getColor(R.color.white))
        binding.pieChartTotal.centerText = "Rates in % (rounded off)"
        binding.pieChartTotal.setCenterTextSize(15f)
        binding.pieChartTotal.setUsePercentValues(true)
        binding.pieChartTotal.legend.isEnabled = false
        binding.pieChartTotal.description.isEnabled = false
        binding.pieChartTotal.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e!!.equalTo(PieEntry(active))) {
                    binding.activeSummary.textSize = 18f
                    binding.deceasedSummary.textSize = 16f
                    binding.recoveredSummary.textSize = 16f

                } else if (e.equalTo(PieEntry(recovered))) {
                    binding.activeSummary.textSize = 16f
                    binding.deceasedSummary.textSize = 16f
                    binding.recoveredSummary.textSize = 18f
                }
                if (e.equalTo(PieEntry(deceased))) {
                    binding.activeSummary.textSize = 16f
                    binding.deceasedSummary.textSize = 18f
                    binding.recoveredSummary.textSize = 16f
                }
            }

            override fun onNothingSelected() {
                binding.activeSummary.textSize = 16f
                binding.deceasedSummary.textSize = 16f
                binding.recoveredSummary.textSize = 16f
            }
        })
        binding.pieChartTotal.invalidate()
    }


    private fun setUpRecentPieChart(active: Float, recovered: Float, deceased: Float) {
        var pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(active))
        pieEntries.add(PieEntry(recovered))
        pieEntries.add(PieEntry(deceased))

        var dataSet = PieDataSet(pieEntries, "")
        var blue: Int = resources.getColor(R.color.blue)
        var red: Int = resources.getColor(R.color.red)
        var green: Int = resources.getColor(R.color.green)
        var colors = ArrayList<Int>()
        colors.add(blue)
        colors.add(green)
        colors.add(red)
        dataSet.colors = colors
        var pieData = PieData(dataSet)
        binding.pieChartRecent.data = pieData
        binding.pieChartRecent.data.setValueTextSize(13f)
        binding.pieChartRecent.data.setValueTextColor(resources.getColor(R.color.white))
        binding.pieChartRecent.centerText = "Rates in % (rounded off)"
        binding.pieChartRecent.setCenterTextSize(15f)
        binding.pieChartRecent.setUsePercentValues(true)
        binding.pieChartRecent.legend.isEnabled = false
        binding.pieChartRecent.description.isEnabled = false
        binding.pieChartRecent.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e!!.equalTo(PieEntry(active))) {
                    binding.dailyNew.textSize = 18f
                    binding.dailyRecovered.textSize = 16f
                    binding.dailyDeceased.textSize = 16f

                } else if (e.equalTo(PieEntry(recovered))) {
                    binding.dailyNew.textSize = 16f
                    binding.dailyRecovered.textSize = 18f
                    binding.dailyDeceased.textSize = 16f
                }
                if (e.equalTo(PieEntry(deceased))) {
                    binding.dailyNew.textSize = 16f
                    binding.dailyRecovered.textSize = 16f
                    binding.dailyDeceased.textSize = 18f
                }
            }

            override fun onNothingSelected() {
                binding.dailyNew.textSize = 16f
                binding.dailyRecovered.textSize = 16f
                binding.recoveredSummary.textSize = 16f
            }
        })
        binding.pieChartRecent.invalidate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
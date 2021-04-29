package com.kunalfarmah.covid_19_info_dashboard.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kunalfarmah.covid_19_info_dashboard.databinding.ItemHistoryDateBinding
import com.kunalfarmah.covid_19_info_dashboard.databinding.ItemSummaryDashboardBinding
import com.kunalfarmah.covid_19_info_dashboard.retrofit.HistoricalSummary
import com.kunalfarmah.covid_19_info_dashboard.retrofit.Summary
import com.kunalfarmah.covid_19_info_dashboard.room.CovidEntity
import com.kunalfarmah.covid_19_info_dashboard.room.CovidHistoryEntity
import com.kunalfarmah.covid_19_info_dashboard.room.HistorySummary
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat
import java.text.SimpleDateFormat

@ExperimentalCoroutinesApi
class HistoryAdapter(context: Activity?, list: List<HistorySummary>) :
    RecyclerView.Adapter<HistoryAdapter.DashboardVH>() {

    var latestList: List<HistorySummary> = list
    var mContext: Activity? = context

    class DashboardVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemHistoryDateBinding = ItemHistoryDateBinding.bind(itemView)
        val df = DecimalFormat("##,##,###")
        val dateFormatter1: SimpleDateFormat =  SimpleDateFormat("yyyy-MM-dd")
        val dateFormatter2: SimpleDateFormat = SimpleDateFormat("dd/MM/yyy")
        @ExperimentalCoroutinesApi
        fun bind(date: String, summary: HistoricalSummary) {
            binding.date.text = dateFormatter2.format(dateFormatter1.parse(date)!!)
            binding.total.text = String.format("Total:\n%s",df.format(Integer.parseInt(summary.total.toString())))
            binding.recovered.text = String.format("Recovered:\n%s",df.format(Integer.parseInt(summary.discharged.toString())))
            binding.deceased.text = String.format("Deceased:\n%s",df.format(Integer.parseInt(summary.deaths.toString())))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardVH {
        return DashboardVH(
            ItemHistoryDateBinding.inflate(
                LayoutInflater.from(mContext), parent, false
            ).root

        )
    }

    override fun onBindViewHolder(holder: DashboardVH, position: Int) {
        var history = latestList[position]
        var summary = Gson().fromJson(history.summary,HistoricalSummary::class.java)
        holder.bind(history.date,summary)
        holder.binding.layout.setOnClickListener {
            (mContext as MainActivity).goToStateHistory(history.date, summary.total.toString()
                ,summary.discharged.toString(),summary.deaths.toString())
        }
    }

    override fun getItemCount(): Int {
        return latestList.size
    }
}
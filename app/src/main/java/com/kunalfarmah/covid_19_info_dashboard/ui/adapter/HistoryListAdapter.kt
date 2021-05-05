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
import com.kunalfarmah.covid_19_info_dashboard.room.HistoryListEntity
import com.kunalfarmah.covid_19_info_dashboard.room.HistorySummary
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat
import java.text.SimpleDateFormat

@ExperimentalCoroutinesApi
class HistoryListAdapter(context: Activity?, list: List<HistoryListEntity>) :
    RecyclerView.Adapter<HistoryListAdapter.DashboardVH>() {

    var latestList: List<HistoryListEntity> = list
    var mContext: Activity? = context

    class DashboardVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemHistoryDateBinding = ItemHistoryDateBinding.bind(itemView)
        val df = DecimalFormat("##,##,###")

        @ExperimentalCoroutinesApi
        fun bind(history: HistoryListEntity) {
            binding.date.text = history.date
            binding.total.text = String.format(
                "Total:\n%s",
                df.format(Integer.parseInt(history.totalconfirmed.toString()))
            )
            binding.recovered.text = String.format(
                "Recovered:\n%s",
                df.format(Integer.parseInt(history.totalrecovered.toString()))
            )
            binding.deceased.text = String.format(
                "Deceased:\n%s",
                df.format(Integer.parseInt(history.totaldeceased.toString()))
            )

            binding.dailyTotal.text = String.format(
                "Cases:\n%s",
                df.format(Integer.parseInt(history.dailyconfirmed.toString()))
            )
            binding.dailyRecovered.text = String.format(
                "Recoveries:\n%s",
                df.format(Integer.parseInt(history.dailyrecovered.toString()))
            )
            binding.dailyDeceased.text = String.format(
                "Deaths:\n%s",
                df.format(Integer.parseInt(history.dailydeceased.toString()))
            )
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
        holder.bind(history)
        holder.binding.layout.setOnClickListener {
            (mContext as MainActivity).goToStateHistory(
                history.dateymd,
                history.totalconfirmed.toString(),
                history.totalrecovered.toString(),
                history.totaldeceased.toString(),
                history.dailyconfirmed.toString(),
                history.dailyrecovered.toString(),
                history.dailydeceased.toString()
            )
        }
    }

    override fun getItemCount(): Int {
        return latestList.size
    }
}
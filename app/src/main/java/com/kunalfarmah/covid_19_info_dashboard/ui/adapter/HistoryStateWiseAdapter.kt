package com.kunalfarmah.covid_19_info_dashboard.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kunalfarmah.covid_19_info_dashboard.databinding.ItemSummaryDashboardBinding
import com.kunalfarmah.covid_19_info_dashboard.room.CovidEntity
import com.kunalfarmah.covid_19_info_dashboard.room.CovidHistoryEntity
import java.text.DecimalFormat

class HistoryStateWiseAdapter(context: Context?, list: List<CovidHistoryEntity>) :
    RecyclerView.Adapter<HistoryStateWiseAdapter.DashboardVH>() {

    var latestList: List<CovidHistoryEntity> = list
    var mContext: Context? = context

    class DashboardVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemSummaryDashboardBinding = ItemSummaryDashboardBinding.bind(itemView)
        val df = DecimalFormat("##,##,###")
        fun bind(case: CovidHistoryEntity) {
            binding.name.text = case.state
            binding.total.text =
                String.format("Total Cases: %s", df.format(case.total))
            binding.recovered.text =
                String.format("Recovered:\n%s", df.format(Integer.parseInt(case.recovered)))
            binding.deceased.text =
                String.format("Deceased:\n%s", df.format(Integer.parseInt(case.deceased)))
            binding.indian.text =
                String.format("Indian:\n%s", df.format(Integer.parseInt(case.confirmedIndian)))
            binding.foriegn.text =
                String.format("Foreign:\n%s", df.format(Integer.parseInt(case.confirmedForeign)))

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardVH {
        return DashboardVH(
            ItemSummaryDashboardBinding.inflate(
                LayoutInflater.from(mContext), parent, false
            ).root
        )
    }

    override fun onBindViewHolder(holder: DashboardVH, position: Int) {
        var case = latestList[position]
        holder.binding.active.visibility = View.GONE
        holder.binding.infoLayout.weightSum = 2f
        holder.bind(case)
    }

    override fun getItemCount(): Int {
        return latestList.size
    }
}
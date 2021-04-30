package com.kunalfarmah.covid_19_info_dashboard.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kunalfarmah.covid_19_info_dashboard.databinding.ItemSummaryDashboardBinding
import com.kunalfarmah.covid_19_info_dashboard.room.CovidEntity
import java.text.DecimalFormat

class DashboardAdapter(context: Context?, list: List<CovidEntity>) :
    RecyclerView.Adapter<DashboardAdapter.DashboardVH>() {

    var latestList: List<CovidEntity> = list
    var mContext: Context? = context

    class DashboardVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemSummaryDashboardBinding = ItemSummaryDashboardBinding.bind(itemView)
        val df = DecimalFormat("##,##,###");
        fun bind(case: CovidEntity) {
            binding.name.text = case.state
            binding.total.text =
                String.format("Total Cases: %s", df.format(case.total))
            if(case.active.isNullOrEmpty()){
                binding.active.visibility = View.GONE
                binding.infoLayout.weightSum = 2f
            }
            else {
                binding.active.visibility = View.VISIBLE
                binding.infoLayout.weightSum = 3f
                binding.active.text =
                    String.format("Active:\n%s", df.format(Integer.parseInt(case.active)))
            }
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
        holder.bind(case)
    }

    override fun getItemCount(): Int {
        return latestList.size
    }
}
package com.kunalfarmah.covid_19_info_dashboard.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentHistoryBinding
import com.kunalfarmah.covid_19_info_dashboard.room.CovidEntity
import com.kunalfarmah.covid_19_info_dashboard.room.HistorySummary
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.DashboardAdapter
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.HistoryAdapter
import com.kunalfarmah.covid_19_info_dashboard.ui.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class HistoryFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var mAdapter: HistoryAdapter
    private var list: List<HistorySummary>? = null

    companion object {
        val TAG = "HistoryFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater)
        viewModel.getHistoryData()

        activity?.actionBar?.title = "History"
        binding.loading.visibility = View.VISIBLE
        binding.loading.startShimmerAnimation()
        binding.dateRecycler.visibility = View.GONE

        viewModel.historyData.observe(viewLifecycleOwner, {
            if (it != null) {
                list=it
                binding.loading.stopShimmerAnimation()
                binding.loading.visibility = View.GONE
                binding.dateRecycler.visibility = View.VISIBLE
                mAdapter = HistoryAdapter(activity, it)
                binding.dateRecycler.setHasFixedSize(true)
                binding.dateRecycler.layoutManager = LinearLayoutManager(context)
                binding.dateRecycler.adapter = mAdapter
            }
        })

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var tempAdapter: HistoryAdapter? = null
            var temp: List<HistorySummary>? = null
            val dateFormatter1: SimpleDateFormat =  SimpleDateFormat("yyyy-MM-dd")
            val dateFormatter2: SimpleDateFormat = SimpleDateFormat("dd/MM/yyy")
            override fun onQueryTextSubmit(query: String): Boolean {
                temp = ArrayList<HistorySummary>()
                for (case in list!!) {

                    val date_ = dateFormatter2.format(dateFormatter1.parse(case.date)!!)
                    if (date_.contains(query, true))
                        (temp as ArrayList<HistorySummary>).add(case)
                }
                tempAdapter = HistoryAdapter(activity, temp as ArrayList<HistorySummary>)
                binding.dateRecycler.adapter = tempAdapter
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                temp = ArrayList<HistorySummary>()
                for (case in list!!) {
                    val date_ = dateFormatter2.format(dateFormatter1.parse(case.date)!!)
                    if (date_.contains(newText, true))
                        (temp as ArrayList<HistorySummary>).add(case)
                }
                tempAdapter = HistoryAdapter(activity, temp as ArrayList<HistorySummary>)
                binding.dateRecycler.adapter = tempAdapter
                return true
            }
        })
        return binding.root
    }
}
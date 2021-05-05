package com.kunalfarmah.covid_19_info_dashboard.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentHistoryBinding
import com.kunalfarmah.covid_19_info_dashboard.room.HistoryListEntity
import com.kunalfarmah.covid_19_info_dashboard.room.HistorySummary
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.HistoryAdapter
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.HistoryListAdapter
import com.kunalfarmah.covid_19_info_dashboard.util.AppUtil
import com.kunalfarmah.covid_19_info_dashboard.util.Constants
import com.kunalfarmah.covid_19_info_dashboard.viewModel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class HistoryFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var mAdapter: HistoryAdapter
    private lateinit var mAdapter2: HistoryListAdapter

    private var list: List<HistorySummary>? = null
    private var history: List<HistoryListEntity>? = null
    private var sPref: SharedPreferences? = null

    companion object {
        val TAG = "HistoryFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getHistoryData()
        viewModel.getHistoryList()
        sPref = activity?.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
        binding = FragmentHistoryBinding.inflate(inflater)
        if (!viewModel.historyData.value.isNullOrEmpty())
//            list = viewModel.historyData.value
            history = viewModel.historyList.value
        if (!list.isNullOrEmpty()) {
//            setView(list!!)
            setViewList(history!!)
        } else {
            binding.loading.visibility = View.VISIBLE
            binding.loading.startShimmerAnimation()
            binding.dateRecycler.visibility = View.GONE
        }

        activity?.actionBar?.title = "History"

        /*viewModel.historyData.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                list = it
                setView(list!!)
            }
        })*/

        viewModel.historyList.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                history = it.sortedWith(HistoryListComparator())
                setViewList(history!!)
            }
        })

        var tempAdapter: HistoryListAdapter? = null
        var temp: List<HistoryListEntity>? = null
        val dateFormatter1: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateFormatter2: SimpleDateFormat = SimpleDateFormat("dd/MM/yyy")

        binding.searchEt.isSelected = false
        binding.searchEt.setOnClickListener {
            binding.close.visibility = View.VISIBLE
        }
        binding.searchEt.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.close.visibility = View.VISIBLE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                temp = ArrayList<HistoryListEntity>()
                for (case in history!!) {

//                    val date_ = dateFormatter2.format(dateFormatter1.parse(case.date)!!)
                    if (case.date.contains(s.toString(), true))
                        (temp as ArrayList<HistoryListEntity>).add(case)
                }
                tempAdapter = HistoryListAdapter(activity, temp as ArrayList<HistoryListEntity>)
                binding.dateRecycler.adapter = tempAdapter
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.search.setOnClickListener {
            AppUtil.hideSoftKeyboard(requireActivity())
            temp = ArrayList<HistoryListEntity>()
            for (case in history!!) {
//                val date_ = dateFormatter2.format(dateFormatter1.parse(case.date)!!)
                if (case.date.contains(binding.searchEt.text.toString(), true))
                    (temp as ArrayList<HistoryListEntity>).add(case)
            }
            tempAdapter = HistoryListAdapter(activity, temp as ArrayList<HistoryListEntity>)
            binding.dateRecycler.adapter = tempAdapter
        }

        binding.close.setOnClickListener {
            binding.searchEt.clearFocus()
            binding.searchEt.text = null
//            binding.dateRecycler.adapter = mAdapter
            binding.dateRecycler.adapter = mAdapter2
            AppUtil.hideSoftKeyboard(requireActivity())
            binding.close.visibility = View.GONE
        }

        return binding.root
    }

    private fun setView(list: List<HistorySummary>) {
        binding.loading.stopShimmerAnimation()
        binding.loading.visibility = View.GONE
        binding.dateRecycler.visibility = View.VISIBLE
        mAdapter = HistoryAdapter(activity, list)
        binding.dateRecycler.setHasFixedSize(true)
        binding.dateRecycler.layoutManager = LinearLayoutManager(context)
        binding.dateRecycler.adapter = mAdapter
    }

    private fun setViewList(list: List<HistoryListEntity>) {
        binding.loading.stopShimmerAnimation()
        binding.loading.visibility = View.GONE
        binding.dateRecycler.visibility = View.VISIBLE
        mAdapter2 = HistoryListAdapter(activity, list)
        binding.dateRecycler.setHasFixedSize(true)
        binding.dateRecycler.layoutManager = LinearLayoutManager(context)
        binding.dateRecycler.adapter = mAdapter2
    }

    override fun onResume() {
        super.onResume()
        val navigationView = activity?.findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.menu.findItem(R.id.nav_home).isChecked = false
    }

    class HistoryListComparator:Comparator<HistoryListEntity>{
        override fun compare(o1: HistoryListEntity?, o2: HistoryListEntity?): Int {
            return o2?.dateymd?.compareTo(o1?.dateymd!!)!!
        }

    }
}
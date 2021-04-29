package com.kunalfarmah.covid_19_info_dashboard.ui.history

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentHistoryBinding
import com.kunalfarmah.covid_19_info_dashboard.room.HistorySummary
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
        list = viewModel.historyData.value
        if(!list.isNullOrEmpty()){
            setView(list!!)
        }
        else{
            binding.loading.visibility = View.VISIBLE
            binding.loading.startShimmerAnimation()
            binding.dateRecycler.visibility = View.GONE
        }

        activity?.actionBar?.title = "History"
        viewModel.getHistoryData()

        viewModel.historyData.observe(viewLifecycleOwner, {
            if (it != null) {
                list = it
                setView(it)
            }
        })

        var tempAdapter: HistoryAdapter? = null
        var temp: List<HistorySummary>? = null
        val dateFormatter1: SimpleDateFormat =  SimpleDateFormat("yyyy-MM-dd")
        val dateFormatter2: SimpleDateFormat = SimpleDateFormat("dd/MM/yyy")

        binding.searchEt.isSelected = false
        binding.searchEt.setOnClickListener{
            binding.close.visibility = View.VISIBLE
        }
        binding.searchEt.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.close.visibility = View.VISIBLE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                temp = ArrayList<HistorySummary>()
                for (case in list!!) {

                    val date_ = dateFormatter2.format(dateFormatter1.parse(case.date)!!)
                    if (date_.contains(s.toString(), true))
                        (temp as ArrayList<HistorySummary>).add(case)
                }
                tempAdapter = HistoryAdapter(activity, temp as ArrayList<HistorySummary>)
                binding.dateRecycler.adapter = tempAdapter
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.search.setOnClickListener {
            hideSoftKeyboard(requireActivity())
            temp = ArrayList<HistorySummary>()
            for (case in list!!) {
                val date_ = dateFormatter2.format(dateFormatter1.parse(case.date)!!)
                if (date_.contains(binding.searchEt.text.toString(), true))
                    (temp as ArrayList<HistorySummary>).add(case)
            }
            tempAdapter = HistoryAdapter(activity, temp as ArrayList<HistorySummary>)
            binding.dateRecycler.adapter = tempAdapter
        }

        binding.close.setOnClickListener{
            binding.searchEt.clearFocus()
            binding.searchEt.text = null
            binding.dateRecycler.adapter = mAdapter
            binding.close.visibility = View.GONE
        }

        return binding.root
    }

    private fun setView(list: List<HistorySummary>){
        binding.loading.stopShimmerAnimation()
        binding.loading.visibility = View.GONE
        binding.dateRecycler.visibility = View.VISIBLE
        mAdapter = HistoryAdapter(activity, list)
        binding.dateRecycler.setHasFixedSize(true)
        binding.dateRecycler.layoutManager = LinearLayoutManager(context)
        binding.dateRecycler.adapter = mAdapter
    }

    fun hideSoftKeyboard(activity: Activity) {
        if (activity.currentFocus == null) {
            return
        }
        val inputMethodManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    }
}
package com.kunalfarmah.covid_19_info_dashboard.listener

import com.kunalfarmah.covid_19_info_dashboard.retrofit.HistoryResponse

interface HistoryListener {
    fun showHistory(list:HistoryResponse)
    fun saveHistory(list:HistoryResponse)
}
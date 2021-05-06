package com.kunalfarmah.covid_19_info_dashboard.repository

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.kunalfarmah.covid_19_info_dashboard.util.Constants
import com.kunalfarmah.covid_19_info_dashboard.retrofit.Api
import com.kunalfarmah.covid_19_info_dashboard.room.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class CovidRepository
constructor(
    private val covidDao: CovidDao,
    private val retrofit: Api
) {


    suspend fun fetchLatestData(sPref: SharedPreferences) {
//        val result = retrofit.getOfficialLatest()
        val result = retrofit.getLatest(Constants.LATEST_URL)
        var history = result.casesTimeSeries?.reversed()
        var statewise = result.statewise
        var summary = statewise?.get(0)
        sPref.edit().putString(Constants.LATEST_SUMMARY, Gson().toJson(summary))
            .apply()
        sPref.edit().putString(Constants.DATE_HISTORY, Gson().toJson(history)).apply()
        sPref.edit().putString(Constants.LAST_REFRESHED, summary?.lastupdatedtime).apply()
        CoroutineScope(Dispatchers.IO).launch {

            for (case in statewise!!) {
                if (case?.state.equals("State Unassigned") || case?.state.equals("Total"))
                    continue
                covidDao.insertLatest(
                    CovidEntity(
                        case?.state.toString(),
                        "0",
                        "0",
                        case?.recovered.toString(),
                        case?.deaths.toString(),
                        case?.active.toString(),
                        Integer.parseInt(case?.confirmed.toString()),
                        case?.deltarecovered.toString(),
                        case?.deltadeaths.toString(),
                        case?.deltaconfirmed.toString()
                    )
                )
            }
            for (value in history!!) {
                if (value?.dateymd == "2020-03-09")
                    break
                covidDao.insertHistoryList(
                    HistoryListEntity(
                        value?.date.toString(),
                        value?.dailyconfirmed.toString(),
                        value?.dailydeceased.toString(),
                        value?.dailyrecovered.toString(),
                        value?.dateymd.toString(),
                        value?.totalconfirmed.toString(),
                        value?.totaldeceased.toString(),
                        value?.totalrecovered.toString()
                    )
                )
            }
        }
    }


    suspend fun fetchContacts(sPref: SharedPreferences) {
        val result = retrofit.getContacts()
        sPref.edit().putString(Constants.CONTACTS, Gson().toJson(result.data)).apply()

    }

    suspend fun getLatestData(): List<CovidEntity> {
        return covidDao.getLatestCases()
    }

    suspend fun fetchHistory() {
        val result = retrofit.getHistory()

        CoroutineScope(Dispatchers.IO).launch {
            for (record in result.data?.reversed()!!) {
                var day = record?.day
                Log.e("History", day!!)
                var summary = record?.summary
                for (case in record?.regional!!) {
                    covidDao.insertHistory(
                        CovidHistoryEntity(
                            day + case?.loc.toString(),
                            day.toString(),
                            Gson().toJson(summary),
                            case?.loc.toString(),
                            case?.confirmedCasesIndian.toString(),
                            case?.confirmedCasesForeign.toString(),
                            case?.discharged.toString(),
                            case?.deaths.toString(),
                            case?.totalConfirmed!!
                        )
                    )
                }
            }
        }
    }

    suspend fun getHistoryList(): List<HistoryListEntity> {
        return covidDao.getHistoryList()
    }

    suspend fun getHistoryData(): List<HistorySummary> {
        return covidDao.getHistorySummary()
    }

    suspend fun getHistoryByDate(date: String): List<CovidHistoryEntity> {
        return covidDao.getHistoryByDate(date)
    }


}
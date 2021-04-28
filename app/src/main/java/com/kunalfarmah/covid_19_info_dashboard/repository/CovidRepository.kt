package com.kunalfarmah.covid_19_info_dashboard.repository
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.listener.LatestListener
import com.kunalfarmah.covid_19_info_dashboard.retrofit.Api
import com.kunalfarmah.covid_19_info_dashboard.room.CovidDao
import com.kunalfarmah.covid_19_info_dashboard.room.CovidEntity
import com.kunalfarmah.covid_19_info_dashboard.room.CovidHistoryEntity
import com.kunalfarmah.covid_19_info_dashboard.room.HistorySummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CovidRepository
constructor(
    private val covidDao: CovidDao,
    private val covidRetrofit: Api
) {


    suspend fun fetchLatestData(sPref:SharedPreferences){
        val result = covidRetrofit.getOfficialLatest()
        sPref.edit().putString(Constants.LATEST_SUMMARY, Gson().toJson(result.data?.summary)).apply()
        if(!result.data?.unofficialSummary.isNullOrEmpty())
            sPref.edit().putString(Constants.LATEST_ACTIVE,result.data?.unofficialSummary?.get(0)?.active.toString()).apply()
            CoroutineScope(Dispatchers.IO).launch{
            for(case in result.data?.regional!!){
                if(case?.loc.equals("State Unassigned"))
                    continue
                covidDao.insertLatest(CovidEntity(case?.loc.toString(),case?.confirmedCasesIndian.toString(),case?.confirmedCasesForeign.toString(),case?.discharged.toString(),case?.deaths.toString(),"",case?.totalConfirmed.toString()))
            }
        }
    }

    suspend fun fetchActiveData(listener: LatestListener){
        val result = covidRetrofit.getUnofficialLatestData()
        CoroutineScope(Dispatchers.IO).launch{
            for(case in result.data?.statewise!!){
                if(case?.state.equals("State Unassigned"))
                    continue
                Log.d("Active",case?.state+" : "+case?.active.toString())
                covidDao.insertActive(case?.active.toString(),case?.state.toString())
            }
        }.invokeOnCompletion { listener.goForward() }

    }

    suspend fun fetchContacts(sPref:SharedPreferences) {
        val result = covidRetrofit.getContacts()
        sPref.edit().putString(Constants.CONTACTS,Gson().toJson(result.data)).apply()

    }

    suspend fun getLatestData(): List<CovidEntity>{
        return covidDao.getLatestCases()
    }

    suspend fun fetchHistory(){
        val result = covidRetrofit.getHistory()

        CoroutineScope(Dispatchers.IO).launch {
            for(record in result.data!!){
                var day = record?.day
                var summary = record?.summary
                for(case in record?.regional!!){
                    covidDao.insertHistory(CovidHistoryEntity(day+case?.loc.toString(),day.toString(),Gson().toJson(summary),case?.loc.toString(),case?.confirmedCasesIndian.toString(),
                    case?.confirmedCasesForeign.toString(),case?.discharged.toString(),case?.deaths.toString(),case?.totalConfirmed.toString()))
                }
            }
        }
    }

    suspend fun getHistoryData(): List<HistorySummary>{
        return covidDao.getHistorySummary()
    }

    suspend fun getHistoryByDate(date:String):List<CovidHistoryEntity>{
        return covidDao.getHistoryByDate(date)
    }



}
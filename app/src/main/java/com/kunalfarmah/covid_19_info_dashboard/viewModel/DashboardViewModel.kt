package com.kunalfarmah.covid_19_info_dashboard.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.google.gson.Gson
import com.kunalfarmah.covid_19_info_dashboard.listener.HistoryListener
import com.kunalfarmah.covid_19_info_dashboard.util.Constants
import com.kunalfarmah.covid_19_info_dashboard.listener.LatestListener
import com.kunalfarmah.covid_19_info_dashboard.model.HistoryDates
import com.kunalfarmah.covid_19_info_dashboard.repository.CovidRepository
import com.kunalfarmah.covid_19_info_dashboard.retrofit.ContactsData
import com.kunalfarmah.covid_19_info_dashboard.retrofit.HistoryResponse
import com.kunalfarmah.covid_19_info_dashboard.room.CovidEntity
import com.kunalfarmah.covid_19_info_dashboard.room.CovidHistoryEntity
import com.kunalfarmah.covid_19_info_dashboard.room.HistoryListEntity
import com.kunalfarmah.covid_19_info_dashboard.room.HistorySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DashboardViewModel @Inject
constructor(
    private val covidRepository: CovidRepository,
    application: Application,
    @ApplicationContext context_: Context
) : AndroidViewModel(application) {

    private val context = context_
    private var sharedPreferences:SharedPreferences?=null

    init{
        sharedPreferences = context.getSharedPreferences(Constants.PREFS,Context.MODE_PRIVATE)
    }
    private val _latestData: MutableLiveData<List<CovidEntity>> = MutableLiveData()
    private val _historyData: MutableLiveData<List<HistorySummary>> = MutableLiveData()
    private val _historyList: MutableLiveData<List<HistoryListEntity>> = MutableLiveData()

    private val _historyDateData: MutableLiveData<List<CovidHistoryEntity>> = MutableLiveData()
    private val _goForward: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _contactsData: MutableLiveData<ContactsData> = MutableLiveData()

    private val sPref: SharedPreferences =
        application.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)


    val latestData: MutableLiveData<List<CovidEntity>>
        get() = _latestData

    val historyData: MutableLiveData<List<HistorySummary>>
        get() = _historyData

    val historyList: MutableLiveData<List<HistoryListEntity>>
        get() = _historyList

    val historyDateData: MutableLiveData<List<CovidHistoryEntity>>
        get() = _historyDateData

    val contactsData: MutableLiveData<ContactsData>
        get() = _contactsData

    val goForward: MutableLiveData<Boolean>
        get() = _goForward



    fun fetchLatestData(listener: LatestListener) {
        viewModelScope.launch {
            covidRepository.fetchLatestData(sPref)
        }.invokeOnCompletion {
            listener.goForward()
        }
    }

    /*fun fetchActiveData(latestListener: LatestListener?){
        viewModelScope.launch {
            covidRepository.fetchActiveData(latestListener)
        }.invokeOnCompletion {
            latestListener?.goForward()
        }
    }*/

    fun fetchHistoryData() {
        viewModelScope.launch {
            covidRepository.fetchHistory()
        }
    }

    fun fetchContacts() {
        viewModelScope.launch {
            covidRepository.fetchContacts(sPref)
        }
    }


    fun getLatestData(latestListener: LatestListener?) {
        viewModelScope.launch {
            latestData.value = covidRepository.getLatestData()
        }.invokeOnCompletion {
            latestListener?.goForward()
        }
    }

    fun getHistoryData() {
        viewModelScope.launch {
            historyData.value = covidRepository.getHistoryData().sortedWith(HistoryComparator())
        }
    }

    fun getHistoryList() {
        viewModelScope.launch {
            historyList.value = covidRepository.getHistoryList()
        }
    }

    fun getHistoryByDate(date:String){
        viewModelScope.launch {
            historyDateData.value = covidRepository.getHistoryByDate(date)
        }
    }

    fun getContacts() {
        contactsData.value = Gson().fromJson(
            sPref.getString(Constants.CONTACTS, ""),
            ContactsData::class.java
        )
    }

    class HistoryComparator:Comparator<HistorySummary>{
        override fun compare(o1: HistorySummary?, o2: HistorySummary?): Int {
            return o2?.date?.compareTo(o1?.date!!)!!
        }

    }

}
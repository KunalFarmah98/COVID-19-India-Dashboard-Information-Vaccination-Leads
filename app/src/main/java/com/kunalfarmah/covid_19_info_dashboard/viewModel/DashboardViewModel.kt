package com.kunalfarmah.covid_19_info_dashboard.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.gson.Gson
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.listener.LatestListener
import com.kunalfarmah.covid_19_info_dashboard.repository.CovidRepository
import com.kunalfarmah.covid_19_info_dashboard.retrofit.ContactsData
import com.kunalfarmah.covid_19_info_dashboard.room.CovidEntity
import com.kunalfarmah.covid_19_info_dashboard.room.CovidHistoryEntity
import com.kunalfarmah.covid_19_info_dashboard.room.HistorySummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class DashboardViewModel @ViewModelInject
constructor(
    private val covidRepository: CovidRepository,
    application: Application
) : AndroidViewModel(application) {


    private val _latestData: MutableLiveData<List<CovidEntity>> = MutableLiveData()
    private val _historyData: MutableLiveData<List<HistorySummary>> = MutableLiveData()
    private val _historyDateData: MutableLiveData<List<CovidHistoryEntity>> = MutableLiveData()

    private val _contactsData: MutableLiveData<ContactsData> = MutableLiveData()

    private val sPref: SharedPreferences =
        application.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)


    val latestData: MutableLiveData<List<CovidEntity>>
        get() = _latestData

    val historyData: MutableLiveData<List<HistorySummary>>
        get() = _historyData

    val historyDateData: MutableLiveData<List<CovidHistoryEntity>>
        get() = _historyDateData

    val contactsData: MutableLiveData<ContactsData>
        get() = _contactsData



    fun fetchLatestData() {
        viewModelScope.launch {
            covidRepository.fetchLatestData(sPref)
        }
    }

    fun fetchActiveData(latestListener: LatestListener?){
        viewModelScope.launch {
            covidRepository.fetchActiveData(latestListener)
        }.invokeOnCompletion {
            latestListener?.goForward()
        }
    }

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
            historyData.value = covidRepository.getHistoryData()
        }
    }

    fun getHistoryByDate(date:String){
        viewModelScope.launch {
            historyDateData.value = covidRepository.getHistoryByDate(date)
        }
    }

    fun getContacts() {
        contactsData.value = Gson().fromJson(
            sPref?.getString(Constants.CONTACTS, ""),
            ContactsData::class.java
        )
    }
}
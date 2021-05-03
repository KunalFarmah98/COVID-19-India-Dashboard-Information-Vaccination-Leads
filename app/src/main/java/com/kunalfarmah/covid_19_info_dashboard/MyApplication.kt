package com.kunalfarmah.covid_19_info_dashboard

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {


    companion object {
        private var mContext: Context? = null
        fun getAppContext(): Context? {
            return mContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this.applicationContext
        FirebaseApp.initializeApp(this)
    }

}
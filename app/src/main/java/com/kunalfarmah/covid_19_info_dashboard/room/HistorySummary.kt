package com.kunalfarmah.covid_19_info_dashboard.room

import androidx.room.ColumnInfo

class HistorySummary {
    @ColumnInfo(name = "date")
    lateinit var date:String

    @ColumnInfo(name = "summary")
    lateinit var summary: String
}
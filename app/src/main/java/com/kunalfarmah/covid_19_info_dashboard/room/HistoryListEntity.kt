package com.kunalfarmah.covid_19_info_dashboard.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "covid19_history_table")
class HistoryListEntity(
    @PrimaryKey
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "dailyconfirmed")
    var dailyconfirmed: String,
    @ColumnInfo(name = "dailydeceased")
    var dailydeceased: String,
    @ColumnInfo(name = "dailyrecovered")
    var dailyrecovered: String,
    @ColumnInfo(name = "dateymd")
    var dateymd: String,
    @ColumnInfo(name = "totalconfirmed")
    var totalconfirmed: String,
    @ColumnInfo(name = "totaldeceased")
    var totaldeceased: String,
    @ColumnInfo(name = "totalrecovered")
    var totalrecovered: String
)
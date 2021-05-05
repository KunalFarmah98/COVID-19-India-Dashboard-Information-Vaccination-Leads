package com.kunalfarmah.covid_19_info_dashboard.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "covid19_table")
class CovidEntity(

    @PrimaryKey
    @ColumnInfo(name = "state")
    var state: String,

    @ColumnInfo(name = "confirmedIndian")
    var confirmedIndian: String,

    @ColumnInfo(name = "confirmedForeign")
    var confirmedForeign: String,

    @ColumnInfo(name = "recovered")
    var recovered: String,

    @ColumnInfo(name = "deceased")
    var deceased: String,

    @ColumnInfo(name = "active")
    var active: String,

    @ColumnInfo(name = "total")
    var total: Int,

    @ColumnInfo(name = "dailyRecovered")
    var dailyRecovered: String,

    @ColumnInfo(name = "dailyDeceased")
    var dailyDeceased: String,

    @ColumnInfo(name = "dailyActive")
    var dailyActive: String

)




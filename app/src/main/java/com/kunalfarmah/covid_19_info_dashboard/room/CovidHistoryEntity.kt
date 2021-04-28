package com.kunalfarmah.covid_19_info_dashboard.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "covid19_history")
class CovidHistoryEntity(

        @PrimaryKey
        var id:String,

        @ColumnInfo(name = "date")
        var date:String,

        @ColumnInfo(name = "summary")
        var summary: String,

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

        @ColumnInfo(name="total")
        var total: String

)

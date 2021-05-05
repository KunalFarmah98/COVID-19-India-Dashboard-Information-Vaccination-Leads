package com.kunalfarmah.covid_19_info_dashboard.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CovidEntity::class, CovidHistoryEntity::class, HistoryListEntity::class], version = 7, exportSchema = false)
abstract class CovidDatabase: RoomDatabase() {

    abstract fun covidDao(): CovidDao

    companion object{
        val DATABASE_NAME: String = "covid-19_db"
    }


}
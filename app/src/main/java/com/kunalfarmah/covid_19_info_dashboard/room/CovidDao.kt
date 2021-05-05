package com.kunalfarmah.covid_19_info_dashboard.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CovidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLatest(covidEntity: CovidEntity)

    @Query("UPDATE covid19_Table SET active = :cases WHERE state=:state")
    suspend fun insertActive(cases: String, state: String)

    @Query("SELECT * FROM covid19_table ORDER BY total DESC")
    suspend fun getLatestCases(): List<CovidEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryList(historyListEntity: HistoryListEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(covidHistoryEntity: CovidHistoryEntity): Long

    @Query("SELECT * FROM covid19_history_table")
    suspend fun getHistoryList(): List<HistoryListEntity>

    @Query("SELECT DISTINCT date, summary FROM covid19_history")
    suspend fun getHistorySummary(): List<HistorySummary>

    @Query("SELECT * FROM covid19_history WHERE date = :date ORDER BY total DESC")
    suspend fun getHistoryByDate(date: String): List<CovidHistoryEntity>
}
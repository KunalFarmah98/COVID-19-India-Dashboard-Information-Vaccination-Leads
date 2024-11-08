package com.kunalfarmah.covid_19_info_dashboard.di

import android.content.Context
import androidx.room.Room
import com.kunalfarmah.covid_19_info_dashboard.room.CovidDao
import com.kunalfarmah.covid_19_info_dashboard.room.CovidDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideCovidDb(@ApplicationContext context: Context): CovidDatabase {
        return Room
            .databaseBuilder(
                context,
                CovidDatabase::class.java,
                CovidDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideCovidDAO(covidDatabase: CovidDatabase): CovidDao {
        return covidDatabase.covidDao()
    }
}
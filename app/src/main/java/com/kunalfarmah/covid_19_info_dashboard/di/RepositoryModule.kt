package com.kunalfarmah.covid_19_info_dashboard.di


import com.kunalfarmah.covid_19_info_dashboard.repository.CovidRepository
import com.kunalfarmah.covid_19_info_dashboard.retrofit.Api
import com.kunalfarmah.covid_19_info_dashboard.room.CovidDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        covidDao: CovidDao,
        retrofit: Api,
    ): CovidRepository {
        return CovidRepository(covidDao, retrofit)
    }
}















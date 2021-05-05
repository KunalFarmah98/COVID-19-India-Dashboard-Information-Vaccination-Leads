package com.kunalfarmah.covid_19_info_dashboard.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kunalfarmah.covid_19_info_dashboard.retrofit.Api
import com.kunalfarmah.covid_19_info_dashboard.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RetrofitModule {


    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson:  Gson): Retrofit.Builder {
         val client = OkHttpClient.Builder().build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit.Builder): Api {
        return retrofit
            .build()
            .create(Api::class.java)
    }

}





















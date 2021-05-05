package com.kunalfarmah.covid_19_info_dashboard.retrofit


import retrofit2.http.*

interface Api {

    @GET
    suspend fun getLatest(@Url url:String): DataResponse

    @GET("stats/latest")
    suspend fun getOfficialLatest(): LatestCasesResponse

    @GET("unofficial/covid19india.org/statewise")
    suspend fun getUnofficialLatestData(): LatestUnofficalResponse

    @GET("stats/history")
    suspend fun getHistory(): HistoryResponse

    @GET("contacts")
    suspend fun getContacts(): ContactsResponse

}
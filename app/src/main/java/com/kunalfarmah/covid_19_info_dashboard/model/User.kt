package com.kunalfarmah.covid_19_info_dashboard.model


class User(
    var id: String? = "",
    var name: String? = "",
    var phone: String?=  "",
    var posts: List<String> = ArrayList(),
    var helped: List<String> = ArrayList()
)
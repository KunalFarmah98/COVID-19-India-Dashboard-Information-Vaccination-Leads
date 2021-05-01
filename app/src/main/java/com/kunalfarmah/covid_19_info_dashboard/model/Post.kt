package com.kunalfarmah.covid_19_info_dashboard.model

class Post(
    var id: String =  "",
    var userId: String =  "",
    var userName: String =  "",
    var title: String =  "",
    var body: String =  "",
    var tags: ArrayList<String> = ArrayList(),
    var contacts: ArrayList<String>?= ArrayList(),
    var links: ArrayList<String>?= ArrayList(),
    var image: String? =  "",
    var upvotes: ArrayList<String> = ArrayList(),
    var downvotes: ArrayList<String> = ArrayList(),
    var helpful: ArrayList<String> = ArrayList(),
    var timeStamp: String =  "",
    var location: String =  ""
) {


}
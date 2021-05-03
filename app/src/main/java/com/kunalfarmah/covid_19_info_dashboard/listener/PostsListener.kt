package com.kunalfarmah.covid_19_info_dashboard.listener

import com.kunalfarmah.covid_19_info_dashboard.model.Post

interface PostsListener {
    fun setData(list:List<Post>)
    fun deleted()
}
package com.kunangkunang.app.view

import com.kunangkunang.app.model.news.DetailNews

interface NewsView {
    fun loadDetailNews(data: DetailNews?)
    fun detailNewsFailed()
}
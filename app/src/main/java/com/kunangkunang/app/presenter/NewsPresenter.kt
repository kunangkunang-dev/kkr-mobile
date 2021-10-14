package com.kunangkunang.app.presenter

import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.api.AppRepositoryCallback
import com.kunangkunang.app.model.news.DetailNews
import com.kunangkunang.app.view.NewsView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NewsPresenter(private val scope: CoroutineScope,
                    private val repository: AppRepository,
                    private val view: NewsView) {

    fun loadDetailNews(id: Int): Job {
        return scope.launch {
            repository
                .getDetailNews(id, object : AppRepositoryCallback<DetailNews?> {
                    override fun onDataLoaded(data: DetailNews?) {
                        view.loadDetailNews(data)
                    }

                    override fun onDataError() {
                        view.detailNewsFailed()
                    }
                })
        }
    }
}
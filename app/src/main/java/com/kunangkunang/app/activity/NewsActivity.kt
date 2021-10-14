package com.kunangkunang.app.activity

import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kunangkunang.app.R
import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.helper.hideSystemBar
import com.kunangkunang.app.model.news.DetailNews
import com.kunangkunang.app.presenter.NewsPresenter
import com.kunangkunang.app.view.NewsView
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.coroutines.*
import org.jetbrains.anko.backgroundColor

class NewsActivity : AppCompatActivity(), NewsView {
    private lateinit var presenter: NewsPresenter
    private lateinit var loadNews: Job

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBar()

        setContentView(R.layout.activity_news)
        setSupportActionBar(tb_news)

        initiateTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (scope.isActive) {
            scope.cancel()
        }
    }

    override fun loadDetailNews(data: DetailNews?) {
        data?.data?.content?.let {
            web_news.settings.javaScriptEnabled = true
            web_news.settings.domStorageEnabled = true
            web_news.settings.setAppCacheEnabled(true)
            web_news.settings.loadsImagesAutomatically = true
            web_news.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            web_news.settings.loadWithOverviewMode = true
            web_news.settings.useWideViewPort = true
            web_news.settings.builtInZoomControls = true
            web_news.settings.displayZoomControls = false
            web_news.settings.setSupportZoom(true)
            web_news.settings.defaultTextEncodingName = "utf-8"
            web_news.settings.cacheMode = WebSettings.LOAD_DEFAULT
            web_news.loadUrl(it)
        }
    }

    override fun detailNewsFailed() {
        Log.e("DETAIL NEWS", "Error")
    }

    private fun initiateTask() {
        web_news.backgroundColor = ContextCompat.getColor(this, R.color.colorPrimary)

        presenter = NewsPresenter(scope, AppRepository(), this)

        val id = intent.getIntExtra("news_id", 0)
        loadNews = presenter.loadDetailNews(id)
    }
}

package com.kunangkunang.app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kunangkunang.app.R
import com.kunangkunang.app.activity.NewsActivity
import com.kunangkunang.app.helper.Utilities
import com.kunangkunang.app.model.news.NewsData
import kotlinx.android.synthetic.main.item_news.view.*

class NewsAdapter(private val context: Context, private val data: List<NewsData?>):
    RecyclerView.Adapter<NewsAdapter.NewsAdapterViewHolder>() {

    inner class NewsAdapterViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindData(item: NewsData?) {
            item?.let { news ->
                news.date?.let {
                    itemView.tv_date.text = Utilities.convertNewsDate(it)
                }

                news.title?.let {
                    itemView.tv_headline.text = it
                }

                itemView.cl_news.setOnClickListener {
                    val intent = Intent(context, NewsActivity::class.java)
                    intent.putExtra("news_id", news.id)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapterViewHolder {
        return NewsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: NewsAdapterViewHolder, position: Int) {
        holder.bindData(data[holder.adapterPosition])
    }
}
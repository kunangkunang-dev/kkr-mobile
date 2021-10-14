package com.kunangkunang.app.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kunangkunang.app.R
import com.kunangkunang.app.activity.*
import com.kunangkunang.app.constant.Constants
import com.kunangkunang.app.model.menu.Menu
import kotlinx.android.synthetic.main.item_menu.view.*

class MenuAdapter(private val context: Context, private val data: List<Menu?>) :
    RecyclerView.Adapter<MenuAdapter.MenuAdapterViewHolder>() {

    inner class MenuAdapterViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindData(item: Menu?, index: Int) {
            item?.let { menu ->
                menu.imageId?.let {
                    Glide.with(itemView)
                        .load(it)
                        .into(itemView.img_menu)
                }

                menu.name?.let {
                    itemView.tv_menu.text = it
                    Log.e("MENU NAME", it)
                }

                itemView.cv_menu.setOnClickListener {
                   when (index) {
                       0 -> context.startActivity(Intent(context, TitleActivity::class.java).putExtra("tag", Constants.FNB))
                       1 -> context.startActivity(Intent(context, TitleActivity::class.java).putExtra("tag", Constants.LAUNDRY))
                       2 -> context.startActivity(Intent(context, TitleActivity::class.java).putExtra("tag", Constants.SPA))
                       3 -> context.startActivity(Intent(context, TitleActivity::class.java).putExtra("tag", Constants.AMENITIES))
                   }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuAdapterViewHolder {
        return MenuAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MenuAdapterViewHolder, position: Int) {
        holder.bindData(data[holder.adapterPosition], holder.adapterPosition)
    }
}
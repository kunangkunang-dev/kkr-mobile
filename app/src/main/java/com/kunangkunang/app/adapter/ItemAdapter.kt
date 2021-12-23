package com.kunangkunang.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kunangkunang.app.R
import com.kunangkunang.app.constant.Constants
import com.kunangkunang.app.helper.Utilities
import com.kunangkunang.app.model.amenities.AmenitiesData
import com.kunangkunang.app.model.fnb.FnbCategoryDataFood
import com.kunangkunang.app.model.laundry.LaundryData
import com.kunangkunang.app.model.order.Order
import com.kunangkunang.app.view.OrderView
import kotlinx.android.synthetic.main.item_data.view.*

class ItemAdapter<T>(private val context: Context,
                     private val data: List<T?>,
                     private val roomID: Int?,
                     private val category: String?,
                     private val view: OrderView<Order?>): RecyclerView.Adapter<ItemAdapter<T>.FnbViewHolder>(){

    inner class FnbViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindData(item: T?) {
            itemView.clCardDisabled.visibility = View.GONE

            category?.let {
                when (category) {
                    Constants.FNB -> {
                        item.apply {
                            this as FnbCategoryDataFood

                            this.image?.let {
                                Glide.with(itemView)
                                    .load(it)
                                    .into(itemView.img_data)
                            }

                            this.name?.let {
                                itemView.tv_name.text = it
                            }

                            this.description?.let {
                                itemView.tv_description.text = it
                            }

                            this.price?.let {
                                itemView.tv_price.text = Utilities.getCurrency(it)
                            }

                            itemView.tv_add_item.setOnClickListener {
                                val order = Order(roomID, this.id, this.categoryId, category, this.name, 1, this.price, null)
                                view.addOrder(order)
                            }
                        }
                    }
                    Constants.LAUNDRY -> {
                        item.apply {
                            this as LaundryData

                            this.image?.let {
                                Glide.with(itemView)
                                    .load(it)
                                    .into(itemView.img_data)
                            }

                            this.name?.let {
                                itemView.tv_name.text = it
                            }

                            this.description?.let {
                                itemView.tv_description.text = it
                            }

                            this.price?.let {
                                itemView.tv_price.text = Utilities.getCurrency(it)
                            }

                            itemView.tv_add_item.setOnClickListener {
                                val order = Order(roomID, this.id, null, category, this.name, 1, this.price, null)
                                view.addOrder(order)
                            }
                        }
                    }
                    Constants.AMENITIES -> {
                        item.apply {
                            this as AmenitiesData

                            this.image?.let {
                                Glide.with(itemView)
                                    .load(it)
                                    .into(itemView.img_data)
                            }

                            this.name?.let {
                                itemView.tv_name.text = it
                            }

                            this.description?.let {
                                itemView.tv_description.text = it
                            }

                            this.price?.let {
                                itemView.tv_price.text = Utilities.getCurrency(it)
                            }

                            itemView.tv_add_item.setOnClickListener {
                                val order = Order(roomID, this.id, null, category, this.name, 1, this.price, null)
                                view.addOrder(order)
                            }
                        }
                    }
                    else -> return
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FnbViewHolder {
        return FnbViewHolder(LayoutInflater.from(context).inflate(R.layout.item_data, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: FnbViewHolder, position: Int) {
        holder.bindData(data[holder.adapterPosition])
    }
}
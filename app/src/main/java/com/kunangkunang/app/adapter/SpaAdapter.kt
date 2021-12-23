package com.kunangkunang.app.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kunangkunang.app.R
import com.kunangkunang.app.helper.Utilities
import com.kunangkunang.app.model.order.Order
import com.kunangkunang.app.model.spa.SpaDataSchedules
import com.kunangkunang.app.view.OrderView
import kotlinx.android.synthetic.main.item_data.view.*

class SpaAdapter(private val context: Context,
                 private val data: List<SpaDataSchedules?>,
                 private val roomID: Int?,
                 private val category: String?,
                 var categoryId: Int?,
                 var name: String?,
                 var image: String?,
                 var price: Int?,
                 private val view: OrderView<Order?>): RecyclerView.Adapter<SpaAdapter.SpaAdapterViewHolder>() {
    inner class SpaAdapterViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindData(item: SpaDataSchedules?, position: Int) {
            item?.let { spaDataSchedules ->
                val day = spaDataSchedules.days
                val from = spaDataSchedules.from
                val to = spaDataSchedules.to

                if (spaDataSchedules.spaAvailability!! <= 0) {
                    itemView.clCardDisabled.visibility = View.VISIBLE
                    itemView.tv_add_item.visibility = View.GONE
                }else{
                    itemView.clCardDisabled.visibility = View.GONE
                    itemView.tv_add_item.visibility = View.VISIBLE
                }

                image?.let {
                    Glide.with(itemView)
                        .load(it)
                        .into(itemView.img_data)
                }

                name?.let {
                    itemView.tv_name.text = it
                }

                price?.let {
                    itemView.tv_price.text = Utilities.getCurrency(it)
                }

                if (day != null && from != null && to != null) {
                    itemView.tv_description.text = "$day, $from-$to"
                }

                itemView.tv_add_item.setOnClickListener {
                    val order = Order(roomID, item.id, categoryId, category, name , 1, price, null, day, from, to)
                    view.addOrder(order)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpaAdapterViewHolder {
        return SpaAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_data, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SpaAdapterViewHolder, position: Int) {
        holder.bindData(data[holder.adapterPosition], holder.adapterPosition)
    }
}
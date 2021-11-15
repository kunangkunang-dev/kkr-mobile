package com.kunangkunang.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kunangkunang.app.R
import com.kunangkunang.app.helper.Utilities
import com.kunangkunang.app.model.order.Order
import com.kunangkunang.app.view.OrderView
import kotlinx.android.synthetic.main.item_order.view.*

class OrderAdapter(private val context: Context,
                   private val data: MutableList<Order?>,
                   private val view: OrderView<Order?>) : RecyclerView.Adapter<OrderAdapter.OrderAdapterViewHolder>() {

    inner class OrderAdapterViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindData(item: Order?) {
            item?.let { order ->
                val detail = order.orderDetail
                val spaDate = order.spaDate
                val spaStart = order.spaStart
                val spaEnd = order.spaEnd

                if (detail != null && spaDate != null && spaStart != null && spaEnd != null) {
                    itemView.tv_order_name.text = "$detail \n($spaDate, ${spaStart}-${spaEnd})"
                } else {
                    itemView.tv_order_name.text = detail
                }

                order.orderQuantity?.let {
                    val qty = "x$it"
                    itemView.tv_order_qty.text = qty
                }

                itemView.img_order_delete.setOnClickListener {
                    view.removeOrder(adapterPosition)
                }

                if (adapterPosition == data.size.minus(1)) {
                    if (getTotalPrice() != 0) {
                        itemView.tv_total_price_value.text = Utilities.getCurrency(getTotalPrice())
                        itemView.tv_total_price.visibility = View.VISIBLE
                        itemView.tv_total_price_value.visibility = View.VISIBLE
                        itemView.dvd_price.visibility = View.VISIBLE
                    } else {
                        itemView.tv_total_price.visibility = View.GONE
                        itemView.tv_total_price_value.visibility = View.GONE
                        itemView.dvd_price.visibility = View.GONE
                    }
                } else {
                    itemView.tv_total_price.visibility = View.GONE
                    itemView.tv_total_price_value.visibility = View.GONE
                    itemView.dvd_price.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapterViewHolder {
        return OrderAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: OrderAdapterViewHolder, position: Int) {
        holder.bindData(data[holder.adapterPosition])
    }

    fun getTotalPrice(): Int {
        var totalPrice = 0

        for (item in data) {
            item?.let {
                if (it.orderPrice != null && it.orderQuantity != null) {
                    totalPrice = totalPrice.plus(it.orderPrice!!.times(it.orderQuantity!!))
                }
            }
        }

        return totalPrice
    }
}
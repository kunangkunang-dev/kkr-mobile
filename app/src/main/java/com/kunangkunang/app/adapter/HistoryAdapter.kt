package com.kunangkunang.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kunangkunang.app.R
import com.kunangkunang.app.helper.Utilities
import com.kunangkunang.app.model.history.HistoryData
import com.kunangkunang.app.view.MainView
import kotlinx.android.synthetic.main.item_history.view.*
import org.jetbrains.anko.textColor

class HistoryAdapter (private val context: Context,
                      private val view: MainView,
                      private val data: List<HistoryData?>): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    var totalPrice = 0

    inner class HistoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindData(item: HistoryData?, index: Int) {
            item?.let { it ->
                it.transactionNo?.let {
                    itemView.tv_order_number.text = it
                }

                it.orderDate?.let {
                    itemView.tv_order_date.text = it
                }

                it.status?.let {
                    itemView.tv_order_status.text = it
                }

                it.notes?.let {
                    itemView.tv_order_note.text = it
                }

                it.details?.let {
                    var subtotal = 0

                    for (detail in it) {
                        val detailName = detail?.itemName
                        val detailQty = detail?.qty
                        val price = detail?.price
                        val tvDetail = TextView(context)
                        var itemPrice = 0
                        val schedule = detail?.spaSchedule?.days
                        val from = detail?.spaSchedule?.from
                        val to = detail?.spaSchedule?.to

                        if (detailQty != null && price != null) {
                            itemPrice = detailQty.times(price)
                            subtotal += itemPrice
                        }

                        if (detailName != null && schedule != null && from != null && to != null) {
                            tvDetail.text = "- $detailName ($schedule, $from-$to) x $detailQty ${Utilities.getCurrency(itemPrice)}"
                        } else {
                            tvDetail.text = "- $detailName x $detailQty ${Utilities.getCurrency(itemPrice)}"
                        }

                        tvDetail.textColor = ContextCompat.getColor(context, android.R.color.white)
                        itemView.ln_order_detail.addView(tvDetail)
                    }

                    totalPrice += subtotal
                    itemView.tv_order_subtotal.text = "Subtotal: ${Utilities.getCurrency(subtotal)}"
                }

                if (index == data.size - 1) {
                    view.setTotalPrice(totalPrice)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_history, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bindData(data[holder.adapterPosition], holder.adapterPosition)
    }
}
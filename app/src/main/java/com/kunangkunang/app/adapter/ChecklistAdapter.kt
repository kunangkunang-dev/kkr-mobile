package com.kunangkunang.app.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kunangkunang.app.R
import com.kunangkunang.app.model.login.LoginDataChecklistItem
import kotlinx.android.synthetic.main.item_checklist.view.*

class ChecklistAdapter(private val context: Context,
                       private val data: List<LoginDataChecklistItem?>): RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {
    inner class ChecklistViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindData(item: LoginDataChecklistItem?) {
            item?.let {
                itemView.cb_checklist_item.text = it.itemName

                itemView.tv_checklist_description.text = it.description

                itemView.tv_checklist_qty.text = "x${it.quantity.toString()}"

                itemView.cb_checklist_item.setOnCheckedChangeListener { _, isChecked ->
                    data[adapterPosition]?.state = isChecked
                }

                data[adapterPosition]?.description = ""

                itemView.et_checklist_note.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        data[adapterPosition]?.description = itemView.et_checklist_note.text.toString()
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        // Do nothing
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        // Do nothing
                    }
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        return ChecklistViewHolder(LayoutInflater.from(context).inflate(R.layout.item_checklist, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.bindData(data[holder.adapterPosition])
    }
}
package com.kunangkunang.app.helper

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.kunangkunang.app.R
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

class CustomSpinner(context: Context,
                    resource: Int,
                    objects: MutableList<String>) : ArrayAdapter<String>(context, resource, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getView(position, convertView, parent).apply {
            this.setPadding(0, this.paddingTop, this.paddingRight, this.paddingBottom)

            val tvColor = this as TextView
            tvColor.textColor = ContextCompat.getColor(context, R.color.colorAccent)
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent).apply {
            this.backgroundColor = ContextCompat.getColor(context, android.R.color.black)

            val tvColor = this as TextView
            tvColor.textColor = ContextCompat.getColor(context, R.color.colorAccent)
        }
    }
}
package com.kunangkunang.app.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import com.kunangkunang.app.BuildConfig
import com.kunangkunang.app.R
import kotlinx.android.synthetic.main.custom_notification.view.*
import java.math.BigInteger
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object Utilities {
    fun getCurrency(value: Int): String {
        val decimalFormat = DecimalFormat("#,###.-")
        return "Rp ${decimalFormat.format(value)}"
    }

    fun generateTransactiondate(): String {
        val simpeDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return simpeDateFormat.format(Calendar.getInstance().time)
    }

    fun removeCustomerData(context: Context) {
        val sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("customer")
        editor.apply()
    }

    fun removeRoomData(context: Context) {
        val sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("room")
        editor.apply()
    }

    fun generateIcon(context: Context, count: Int, drawable: Int): Drawable {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_notification, null)
        view.icn_badge.setImageResource(drawable)

        if (count == 0) {
            view.tv_badge.visibility = View.GONE
        } else {
            view.tv_badge.text = count.toString()
        }

        view.apply {
            this.measure(
                View.MeasureSpec.makeMeasureSpec(
                    0,
                    View.MeasureSpec.UNSPECIFIED
                ),
                View.MeasureSpec.makeMeasureSpec(
                    0,
                    View.MeasureSpec.UNSPECIFIED
                )
            )

            this.layout(0, 0, view.measuredWidth, view.measuredHeight)
        }

        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return BitmapDrawable(context.resources, bitmap)
    }

    fun rotateIcon(context: Context, view: View) {
        val rotate = AnimationUtils.loadAnimation(context, R.anim.rotate_animation)
        view.startAnimation(rotate)
    }

    fun fadeOutIcon(context: Context, view: View) {
        val faeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out_animation)
        view.startAnimation(faeOut)
    }

    fun stopAnimation(view: View) {
        view.clearAnimation()
    }

    fun convertNewsDate(date: String): String {
        val oldFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val newFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

        val oldDate = oldFormat.parse(date)
        return newFormat.format(oldDate)
    }
}
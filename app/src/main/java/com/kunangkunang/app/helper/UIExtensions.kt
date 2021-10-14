package com.kunangkunang.app.helper

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat

fun Activity.hideSystemBar() {
    window.apply {
        setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        navigationBarColor = ContextCompat.getColor(this@hideSystemBar, android.R.color.black)
    }
}

fun Activity.setDimensionLarge(dialog: androidx.appcompat.app.AlertDialog) {
    val width = resources.displayMetrics.widthPixels.times(0.80).toInt()
    dialog.window?.let {
        it.setLayout(width, it.attributes.height)
    }
}

fun Activity.setDimensionSuperLarge(dialog: androidx.appcompat.app.AlertDialog) {
    val width = resources.displayMetrics.widthPixels.times(0.90).toInt()
    dialog.window?.let {
        it.setLayout(width, it.attributes.height)
    }
}

fun Activity.setDimensionSmall(dialog: androidx.appcompat.app.AlertDialog) {
    val width = resources.displayMetrics.widthPixels.times(0.50).toInt()
    dialog.window?.let {
        it.setLayout(width, it.attributes.height)
    }
}




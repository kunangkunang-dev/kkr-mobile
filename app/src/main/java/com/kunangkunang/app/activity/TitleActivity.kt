package com.kunangkunang.app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kunangkunang.app.R
import com.kunangkunang.app.constant.Constants
import com.kunangkunang.app.helper.hideSystemBar
import kotlinx.android.synthetic.main.activity_title.*

class TitleActivity : AppCompatActivity() {
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBar()

        setContentView(R.layout.activity_title)
        setSupportActionBar(tb_title)

        // Starting task
        initiateTask()
    }

    private fun initiateTask() {
        // Get intent
        intent?.let { it ->
            it.getStringExtra("image")?.let {
                Glide.with(img_title_detail)
                    .load(it)
                    .into(img_title_detail)
            }

            it.getStringExtra("category")?.let {
                category = it
            }
        }

        // Set button listener
        btn_title.setOnClickListener {
            startActivity(category)
        }
    }

    private fun startActivity(tag: String) {
        // Create intent according to it's category
        when (tag) {
            Constants.FNB ->
                startActivity(Intent(this, FnbActivity::class.java))

            Constants.LAUNDRY ->
                startActivity(Intent(this, LaundryActivity::class.java))

            Constants.SPA ->
                startActivity(Intent(this, SpaActivity::class.java))

            Constants.AMENITIES ->
                startActivity(Intent(this, AmenitiesActivity::class.java))

            Constants.ACTIVITIES ->
                startActivity(Intent(this, ActivitiesActivity::class.java))
        }

        // Finish title activity
        finish()
    }
}

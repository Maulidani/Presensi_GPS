package com.skripsi.presensigps.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.skripsi.presensigps.R
import kotlinx.android.synthetic.main.activity_detail_image.*

class DetailImageActivity : AppCompatActivity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)
        supportActionBar?.title = "Detail foto"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.ic_face)
        val img = intent.getStringExtra("img")
        Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load(img)
            .into(imgDetail)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
package com.skripsi.presensigps.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skripsi.presensigps.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.title = "Daftar"
    }
}
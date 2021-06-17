package com.skripsi.presensigps.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skripsi.presensigps.R
import com.skripsi.presensigps.utils.Constant
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.title = "Profil"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPref = PreferencesHelper(this)

        profileName.setText(sharedPref.getString(Constant.PREF_USER_NAME))
        profilePosition.setText(sharedPref.getString(Constant.PREF_USER_POSITION))
        profileEmail.setText(sharedPref.getString(Constant.PREF_USER_EMAIL))
        profilePassword.setText(sharedPref.getString(Constant.PREF_USER_PASSWORD))

        btnLogout.setOnClickListener {
            sharedPref.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
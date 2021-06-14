package com.skripsi.presensigps.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.apotekku.apotekku.session.PreferencesHelper
import com.skripsi.presensigps.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.title = "Profil Saya"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPref = PreferencesHelper(this)

        btnLogout.setOnClickListener {
            sharedPref.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
        }
//        Toast.makeText(
//            this, "${sharedPref.getString(Constant.PREF_USER_ID)} \n" +
//                    "${sharedPref.getString(Constant.PREF_USER_NAME)} \n" +
//                    "${sharedPref.getString(Constant.PREF_USER_POSITION)} \n" +
//                    "${sharedPref.getString(Constant.PREF_USER_EMAIL)} \n" +
//                    "${sharedPref.getString(Constant.PREF_USER_PASSWORD)}", Toast.LENGTH_SHORT
//        )
//            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
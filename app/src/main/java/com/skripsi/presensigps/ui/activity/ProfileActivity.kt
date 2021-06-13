package com.skripsi.presensigps.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.apotekku.apotekku.session.Constant
import com.skripsi.presensigps.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

//        Toast.makeText(
//            this, "${sharedPref.getString(Constant.PREF_USER_ID)} \n" +
//                    "${sharedPref.getString(Constant.PREF_USER_NAME)} \n" +
//                    "${sharedPref.getString(Constant.PREF_USER_POSITION)} \n" +
//                    "${sharedPref.getString(Constant.PREF_USER_EMAIL)} \n" +
//                    "${sharedPref.getString(Constant.PREF_USER_PASSWORD)}", Toast.LENGTH_SHORT
//        )
//            .show()
    }
}
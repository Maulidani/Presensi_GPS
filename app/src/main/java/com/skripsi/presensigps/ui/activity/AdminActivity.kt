@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.network.ApiClient
import com.skripsi.presensigps.utils.Constant
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var snackbar: Snackbar
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        supportActionBar?.title = "Admin"

        sharedPref = PreferencesHelper(this)

        if (!sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            finish()
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Memuat Informasi...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        tvName.text = sharedPref.getString(Constant.PREF_USER_NAME).toString()

        getInfoAdmin()
        btnOnClick()
    }

    private fun getInfoAdmin() {
        ApiClient.instance.getInfoAdmin().enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                val message = response.body()?.message

                if (value.equals("1")) {
                    tvTotalPresensi.text = response.body()?.total_presence
                    tvTotalReport.text = response.body()?.total_report
                    tvTotalUser.text = response.body()?.total_user
                    btnRefresh.text = response.body()?.time

                    progressDialog.dismiss()

                } else {
                    progressDialog.dismiss()

                    Toast.makeText(
                        this@AdminActivity,
                        message.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                progressDialog.dismiss()

                snackbar = Snackbar.make(
                    parentAdminActivity, t.message.toString(),
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemProfile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                return true
            }
            R.id.itemAbout -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (!sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            finish()
        } else {
            getInfoAdmin()
        }
    }

    private fun btnOnClick() {
        btnRefresh.setOnClickListener {
            progressDialog.show()
            getInfoAdmin()
        }
        cardPresence.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java).putExtra("type", "presence"))
        }
        cardReport.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java).putExtra("type", "report"))
        }
        cardUser.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java).putExtra("type", "user"))
        }
        cardMaps.setOnClickListener {
//            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

}
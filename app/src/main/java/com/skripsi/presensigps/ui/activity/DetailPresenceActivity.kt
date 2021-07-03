@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.ui.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.network.ApiClient
import kotlinx.android.synthetic.main.activity_detail_presence.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.item_report.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPresenceActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog
    private var name: String? = null
    lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_presence)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Memuat informasi...")
        progressDialog.setCancelable(false)

        val idName = intent.getStringExtra("id")
        name = intent.getStringExtra("name")

        supportActionBar?.title = name

        getDetail(idName.toString())
    }

    private fun getDetail(id: String) {
        progressDialog.show()

        ApiClient.instance.getDetailPresence(id).enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                val message = response.body()?.message

                if (value == "1") {
                    Glide.with(this@DetailPresenceActivity)
                        .load(response.body()?.img.toString())
                        .into(imgPresence)
                    tvName2.text = name
                    tvDate.text = response.body()?.date
                    tvTime.text = response.body()?.time
                    tvNotPresensi.visibility = View.INVISIBLE
                }
                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                snackbar = Snackbar.make(
                    parentDetailActivity,
                    t.message.toString(),
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.ui.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.network.ApiClient
import kotlinx.android.synthetic.main.activity_detail_presence.*
import kotlinx.android.synthetic.main.fragment_history2.*
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
        val date = intent.getStringExtra("date")
        val currentdate = intent.getStringExtra("currentDate")
        val today = intent.getStringExtra("today")
        name = intent.getStringExtra("name")

        supportActionBar?.title = name

        getDetail(idName.toString(), date.toString(), currentdate.toString(), today.toString())

    }

    private fun getDetail(id: String, date: String, currentDate: String, today: String) {
        progressDialog.show()

        ApiClient.instance.getDetailPresence(id,today).enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                val history = response.body()?.history

                if (value.equals("1") && date == currentDate) {
                    onResponseSuccess(response.body()!!)

                } else if (history.equals("1")){
                    onResponseSuccess(response.body()!!)
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
                progressDialog.dismiss()
            }
        })
    }

    @SuppressLint("CheckResult")
    private fun onResponseSuccess(body: DataResponse) {

       val requestOptions = RequestOptions()
       requestOptions.placeholder(R.drawable.ic_face)

       Glide.with(this@DetailPresenceActivity)
           .setDefaultRequestOptions(requestOptions)
           .load(body.img)
           .into(imgPresence)
       tvName2.text = name
       tvDate.text = body.date
       tvTime.text = body.time
       tvNotPresensi.visibility = View.INVISIBLE

       imgPresence.setOnClickListener {
           startActivity(
               Intent(this@DetailPresenceActivity, DetailImageActivity::class.java)
                   .putExtra("img", body.img.toString()))
       }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.ui.activity

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.adapter.PresenceAdapter
import com.skripsi.presensigps.adapter.ReportAdapter
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.network.ApiClient
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.fragment_report.rv
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfoActivity : AppCompatActivity() {
    private lateinit var lLayoutManager: LinearLayoutManager
    private lateinit var pAdapter: PresenceAdapter
    private lateinit var rAdapter: ReportAdapter
    private lateinit var snackbar: Snackbar
    private lateinit var progressDialog: ProgressDialog
    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        type = intent.getStringExtra("type").toString()
        supportActionBar?.title = type
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Masuk")
        progressDialog.setMessage("Memuat Informasi...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        lLayoutManager = LinearLayoutManager(this)
        rv.layoutManager = lLayoutManager
        rv.setHasFixedSize(true)
    }

    private fun presence() {
        ApiClient.instance.getPresence().enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                val message = response.body()?.message

                if (value.equals("1")) {
                    pAdapter = PresenceAdapter(response.body()!!.result)
                    rv.adapter = pAdapter

                    progressDialog.dismiss()
                } else {
                    snackbar =
                        Snackbar.make(parentInfoActivity, message.toString(), Snackbar.LENGTH_SHORT)
                    snackbar.show()

                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                progressDialog.dismiss()

                snackbar =
                    Snackbar.make(parentInfoActivity, t.message.toString(), Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        })
    }

    private fun report() {
        ApiClient.instance.getReport().enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                val message = response.body()?.message

                if (value.equals("1")) {
                    rAdapter = ReportAdapter(response.body()!!.result)
                    rv.adapter = rAdapter

                    progressDialog.dismiss()
                } else {
                    snackbar =
                        Snackbar.make(parentInfoActivity, message.toString(), Snackbar.LENGTH_SHORT)
                    snackbar.show()

                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                progressDialog.dismiss()

                snackbar =
                    Snackbar.make(parentInfoActivity, t.message.toString(), Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        })
    }

    override fun onStart() {
        super.onStart()

        when (type) {
            "presence" -> presence()
            "report" -> report()
        }
    }

    override fun onResume() {
        super.onResume()
        when (type) {
            "presence" -> presence()
            "report" -> report()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
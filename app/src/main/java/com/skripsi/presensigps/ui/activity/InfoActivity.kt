@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.adapter.PresenceAdapter
import com.skripsi.presensigps.adapter.ReportAdapter
import com.skripsi.presensigps.adapter.UserAdapter
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.model.Result
import com.skripsi.presensigps.network.ApiClient
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfoActivity : AppCompatActivity(), ReportAdapter.IUserRecycler, UserAdapter.IUserRecycler {
    private lateinit var lLayoutManager: LinearLayoutManager
    private lateinit var lLayoutManagerToday: LinearLayoutManager
    private lateinit var pAdapter: PresenceAdapter
    private lateinit var rAdapter: ReportAdapter
    private lateinit var uAdapter: UserAdapter
    private lateinit var snackbar: Snackbar
    private lateinit var progressDialog: ProgressDialog
    private lateinit var type: String
    private var admin: Boolean? = null
    private var onUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        type = intent.getStringExtra("type").toString()
        admin = intent.getBooleanExtra("admin", false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Memuat Informasi...")
        progressDialog.setCancelable(false)

        lLayoutManager = LinearLayoutManager(this)
        rv.layoutManager = lLayoutManager
        rv.setHasFixedSize(true)
    }

    private fun presence() {
        progressDialog.show()

        ApiClient.instance.getUser("").enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val result = response.body()?.result
                val value = response.body()?.value
                var message = "Sukses"

                if (value.equals("1")) {
                    if (onUpdate) {

                        GlobalScope.launch(context = Dispatchers.Main) {
                            pAdapter = PresenceAdapter(result!!)
                            rv.adapter = pAdapter

                            delay(2000)
                            progressDialog.dismiss()

                            Toast.makeText(
                                this@InfoActivity,
                                message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } else {
                        pAdapter = PresenceAdapter(result!!)
                        rv.adapter = pAdapter

                        progressDialog.dismiss()
                    }

                } else {
                    message = "Gagal"

                    snackbar =
                        Snackbar.make(parentInfoActivity, message, Snackbar.LENGTH_SHORT)
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
        progressDialog.show()

        GlobalScope.launch(context = Dispatchers.Main) {

            ApiClient.instance.getReport("today").enqueue(object : Callback<DataResponse> {
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    val result = response.body()?.result
                    val value = response.body()?.value
                    var message = "Sukses"

                    if (value.equals("1")) {
                        if (onUpdate) {
                            rAdapter =
                                ReportAdapter(result!!, this@InfoActivity)
                            rvToday.adapter = rAdapter

                            Toast.makeText(
                                this@InfoActivity,
                                message,
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            if (response.body()!!.result.isEmpty()) {
                                tvReportNull.visibility = View.VISIBLE
                            }

                        } else {

                            rAdapter = ReportAdapter(result!!, this@InfoActivity)
                            rvToday.adapter = rAdapter

                            if (response.body()!!.result.isEmpty()) {
                                tvReportNull.visibility = View.VISIBLE
                            }
                        }

                    } else {
                        message = "Gagal"

                        snackbar =
                            Snackbar.make(parentInfoActivity, message, Snackbar.LENGTH_SHORT)
                        snackbar.show()

                    }
                }

                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    snackbar =
                        Snackbar.make(
                            parentInfoActivity,
                            t.message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                    snackbar.show()
                }
            })

        }

        ApiClient.instance.getReport("").enqueue(
            object : Callback<DataResponse> {
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    val value = response.body()?.value
                    var message = "Sukses"

                    if (value.equals("1")) {
                        if (onUpdate) {

                            GlobalScope.launch(context = Dispatchers.Main) {
                                rAdapter =
                                    ReportAdapter(response.body()!!.result, this@InfoActivity)
                                rv.adapter = rAdapter

                                delay(2000)
                                progressDialog.dismiss()

                                Toast.makeText(
                                    this@InfoActivity,
                                    message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {

                            rAdapter = ReportAdapter(response.body()!!.result, this@InfoActivity)
                            rv.adapter = rAdapter

                            progressDialog.dismiss()
                        }

                    } else {
                        message = "Gagal"

                        snackbar =
                            Snackbar.make(parentInfoActivity, message, Snackbar.LENGTH_SHORT)
                        snackbar.show()

                        progressDialog.dismiss()
                    }
                }

                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    progressDialog.dismiss()

                    snackbar =
                        Snackbar.make(
                            parentInfoActivity,
                            t.message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                    snackbar.show()
                }
            })
    }

    private fun user() {
        progressDialog.show()

        ApiClient.instance.getUser("").enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                var message = "Sukses"

                if (value.equals("1")) {
                    if (onUpdate) {

                        GlobalScope.launch(context = Dispatchers.Main) {
                            uAdapter = UserAdapter(response.body()!!.result, this@InfoActivity)
                            rv.adapter = uAdapter

                            delay(2000)
                            progressDialog.dismiss()

                            Toast.makeText(
                                this@InfoActivity,
                                message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else {

                        uAdapter = UserAdapter(response.body()!!.result, this@InfoActivity)
                        rv.adapter = uAdapter

                        progressDialog.dismiss()
                    }

                } else {
                    message = "Gagal"

                    snackbar =
                        Snackbar.make(parentInfoActivity, message, Snackbar.LENGTH_SHORT)
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
            "presence" -> {
                supportActionBar?.title = "Presensi"
                presence()
            }
            "report" -> {
                supportActionBar?.title = "Laporan"
                rvToday.visibility = View.VISIBLE
                tvToday.visibility = View.VISIBLE
                tvAll.visibility = View.VISIBLE

                lLayoutManagerToday = LinearLayoutManager(this)
                rvToday.layoutManager = lLayoutManagerToday
                rvToday.setHasFixedSize(true)
                report()
            }
            "user" -> {
                supportActionBar?.title = "Akun"
                user()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        when (type) {
            "presence" -> presence()
            "report" -> report()
            "user" -> user()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (type == "user" && admin == true) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.add_user, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemAdd -> {
                startActivity(Intent(this, RegisterActivity::class.java))
                return true

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun refreshView(dataResult: Result, onUpdate: Boolean, type_: String) {
        this.onUpdate = onUpdate
        when (type_) {
            "report" -> report()
            "user" -> user()
        }
    }
}
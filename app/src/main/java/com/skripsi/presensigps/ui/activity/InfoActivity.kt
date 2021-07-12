@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.adapter.PresenceAdapter
import com.skripsi.presensigps.adapter.ReportAdapter
import com.skripsi.presensigps.adapter.UserAdapter
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.model.Result
import com.skripsi.presensigps.network.ApiClient
import com.skripsi.presensigps.utils.Constant
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class InfoActivity : AppCompatActivity(), ReportAdapter.IUserRecycler, UserAdapter.IUserRecycler {
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var lLayoutManager: LinearLayoutManager
    private lateinit var lLayoutManagerToday: LinearLayoutManager
    private lateinit var pAdapter: PresenceAdapter
    private lateinit var rAdapter: ReportAdapter
    private lateinit var uAdapter: UserAdapter
    private lateinit var snackbar: Snackbar
    private lateinit var progressDialog: ProgressDialog
    private lateinit var progressDialogCetak: ProgressDialog
    private lateinit var type: String
    private var admin: Boolean? = null
    private var onUpdate: Boolean = false

    private lateinit var scaleBitmap: Bitmap
    private lateinit var bitmap: Bitmap
    private val pageWidth = 1200
    private val pageHeight = 2400
    private lateinit var dateTime: Date
    private lateinit var dateFormat: DateFormat
    private val itemCetak = listOf(
        "hari ini",
        "januari",
        "februari",
        "maret",
        "april",
        "mei",
        "juni",
        "juli",
        "agustus",
        "september",
        "oktober",
        "november",
        "desember",
        "semua"
    )
    private val itemYear = listOf(
        "2021",
        "2022",
        "2023"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        sharedPref = PreferencesHelper(this)

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

        ApiClient.instance.getUser("", "sales").enqueue(object : Callback<DataResponse> {
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

        ApiClient.instance.getUser("", "").enqueue(object : Callback<DataResponse> {
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

    private fun printReport(whenReport: String, sWhen: String, yearReport: String, sYear: String) {
        progressDialogCetak = ProgressDialog(this)
        progressDialogCetak.setTitle("Loading")
        progressDialogCetak.setMessage("Memuat Informasi...")
        progressDialogCetak.setCancelable(false)
        progressDialogCetak.show()

        ApiClient.instance.getReportPDF(whenReport, yearReport)
            .enqueue(object : Callback<DataResponse> {
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    val value = response.body()?.value
                    val result = response.body()?.result
                    var message = "Sukses"

                    if (value.equals("1")) {
                        if (!result.isNullOrEmpty()) {
                            if (whenReport == "today") {
                                createPDF(result, whenReport, sWhen, sYear)
                            } else {
                                createPDF(result, whenReport, sWhen, sYear)
                            }
                        } else {
                            snackbar =
                                Snackbar.make(
                                    parentInfoActivity,
                                    "Belum ada laporan",
                                    Snackbar.LENGTH_SHORT
                                )
                            snackbar.show()

                            progressDialog.dismiss()
                        }
                    } else {
                        message = "Gagal"

                        snackbar =
                            Snackbar.make(parentInfoActivity, message, Snackbar.LENGTH_SHORT)
                        snackbar.show()

                        progressDialog.dismiss()
                    }
                    progressDialogCetak.dismiss()
                    cardCetak.visibility = View.INVISIBLE
                }

                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    progressDialogCetak.dismiss()
                    snackbar =
                        Snackbar.make(
                            parentInfoActivity,
                            t.message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                    snackbar.show()
                    cardCetak.visibility = View.INVISIBLE
                }

            })
    }

    @SuppressLint("SimpleDateFormat")
    private fun createPDF(
        result: ArrayList<Result>,
        whenReport: String,
        sWhen: String,
        sYear: String
    ) {
        dateTime = Date()

        val pdfDocument = PdfDocument()
        val paint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        canvas.drawBitmap(scaleBitmap, 0f, 0f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.BLACK
        paint.textSize = 35f
        canvas.drawText("Nama: " + sharedPref.getString(Constant.PREF_USER_NAME), 20f, 590f, paint)

        paint.textAlign = Paint.Align.RIGHT
        dateFormat = SimpleDateFormat("dd/MM/yy")
        canvas.drawText(
            "Tanggal: " + dateFormat.format(dateTime),
            (pageWidth - 20).toFloat(),
            590f,
            paint
        )
        dateFormat = SimpleDateFormat("HH:mm:ss")
        canvas.drawText(
            "Pukul: " + dateFormat.format(dateTime),
            (pageWidth - 20).toFloat(),
            640f,
            paint
        )

        canvas.drawText(
            "laporan: $sWhen, $sYear",
            (pageWidth - 20).toFloat(),
            690f,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRect(20f, 780f, (pageWidth - 20).toFloat(), 860f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.style = Paint.Style.FILL

        var y = 950

        if (whenReport == "today") {
            canvas.drawText("No.", 40f, 830f, paint)
            canvas.drawText("Nama", 150f, 830f, paint)
            canvas.drawText("Melapor", 700f, 830f, paint)
            canvas.drawText("Ket.", 1050f, 830f, paint)
            canvas.drawLine(130f, 790f, 130f, 840f, paint)
            canvas.drawLine(680f, 790f, 680f, 840f, paint)
            canvas.drawLine(1030f, 790f, 1030f, 840f, paint)

            var no = 1
            for (i in result) {
                canvas.drawText(no.toString(), 40f, y.toFloat(), paint)
                canvas.drawText(i.name, 150f, y.toFloat(), paint)
                canvas.drawText(i.time, 700f, y.toFloat(), paint)
                canvas.drawText("--", 1100f, y.toFloat(), paint)

                no += 1
                y += 100
            }
        } else {
            canvas.drawText("No.", 40f, 830f, paint)
            canvas.drawText("Nama", 150f, 830f, paint)
            canvas.drawText("Jumlah Laporan", 700f, 830f, paint)
            canvas.drawText("Ket.", 1050f, 830f, paint)
            canvas.drawLine(130f, 790f, 130f, 840f, paint)
            canvas.drawLine(680f, 790f, 680f, 840f, paint)
            canvas.drawLine(1030f, 790f, 1030f, 840f, paint)

            var no = 1
            for (i in result) {
                canvas.drawText(no.toString(), 40f, y.toFloat(), paint)
                canvas.drawText(i.name, 150f, y.toFloat(), paint)
                canvas.drawText(i.jumlah, 700f, y.toFloat(), paint)
                canvas.drawText("--", 1100f, y.toFloat(), paint)

                no += 1
                y += 100
            }

        }

        pdfDocument.finishPage(page)
        val date = SimpleDateFormat("dd_MM_yy")
        val time = SimpleDateFormat("HH_mm_ss")

        val file = File(
            Environment.getExternalStorageDirectory(),
            "/sales_laporan_tgl_${date.format(dateTime)}_pkl_${time.format(dateTime)}.pdf"
        )

        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        pdfDocument.close()
        Toast.makeText(this, "Periksa file manager anda", Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()

        when (type) {
            "presence" -> {
                supportActionBar?.title = "Presensi"
                presence()
            }
            "report" -> {
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo_header)
                scaleBitmap = Bitmap.createScaledBitmap(bitmap, pageWidth, 518, false)

                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), PackageManager.PERMISSION_GRANTED
                )

                supportActionBar?.title = "Laporan"

                val adapterCetak = ArrayAdapter(this, R.layout.list_dropdown, itemCetak)
                inputCetak.setAdapter(adapterCetak)
                val adapterYear = ArrayAdapter(this, R.layout.list_dropdown, itemYear)
                inputYear.setAdapter(adapterYear)

                rvToday.visibility = View.VISIBLE
                tvCetak.visibility = View.VISIBLE
                tvToday.visibility = View.VISIBLE
                tvAll.visibility = View.VISIBLE

                lLayoutManagerToday = LinearLayoutManager(this)
                rvToday.layoutManager = lLayoutManagerToday
                rvToday.setHasFixedSize(true)
                report()

                tvCetak.setOnClickListener {
                    cardCetak.visibility = View.VISIBLE
                }

                btnCetak.setOnClickListener {
                    val sWhen = inputCetak.text.toString()
                    var whenReport = ""
                    when (sWhen) {
                        "hari ini" -> whenReport = "today"
                        "januari" -> whenReport = "01"
                        "februari" -> whenReport = "02"
                        "maret" -> whenReport = "03"
                        "april" -> whenReport = "04"
                        "mei" -> whenReport = "05"
                        "juni" -> whenReport = "06"
                        "juli" -> whenReport = "07"
                        "agustus" -> whenReport = "08"
                        "september" -> whenReport = "09"
                        "oktober" -> whenReport = "10"
                        "november" -> whenReport = "11"
                        "desember" -> whenReport = "12"
                        "semua" -> whenReport = ""
                    }
                    val sYear = inputYear.text.toString()
                    var yearReport = ""
                    when (sYear) {
                        "2021" -> yearReport = "21"
                        "2022" -> yearReport = "22"
                        "2023" -> yearReport = "23"
                    }

                    when {
                        sWhen == "" -> inputCetak.error = "Pilih terlebih dahulu"
                        sYear == "" -> inputYear.error = "Pilih terlebih dahulu"
                        else -> printReport(whenReport, sWhen, yearReport, sYear)
                    }
                }
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
            "report" -> {
                val adapterCetak = ArrayAdapter(this, R.layout.list_dropdown, itemCetak)
                inputCetak.setAdapter(adapterCetak)
                val adapterYear = ArrayAdapter(this, R.layout.list_dropdown, itemYear)
                inputYear.setAdapter(adapterYear)
                report()
            }
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
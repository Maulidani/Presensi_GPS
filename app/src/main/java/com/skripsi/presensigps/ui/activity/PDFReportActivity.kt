package com.skripsi.presensigps.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.skripsi.presensigps.R
import kotlinx.android.synthetic.main.activity_pdf_report.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class PDFReportActivity : AppCompatActivity() {

    private lateinit var scaleBitmap: Bitmap
    private lateinit var bitmap: Bitmap
    private val pageWidth = 1200
    private val pageHeight = 2010
    private lateinit var dateTime: Date
    private lateinit var dateFormat: DateFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_report)

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo_header)
        scaleBitmap = Bitmap.createScaledBitmap(bitmap, pageWidth, 518, false)

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PackageManager.PERMISSION_GRANTED
        )


        btnPrint.setOnClickListener {
            createPDF()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createPDF() {
        dateTime = Date()

        val pdfDocument = PdfDocument()
        val paint = Paint()

        val pageInfo = PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        canvas.drawBitmap(scaleBitmap, 0f, 0f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.BLACK
        paint.textSize = 35f
        canvas.drawText("Nama: " + "Admin Lapi", 20f, 590f, paint)

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

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRect(20f, 780f, (pageWidth - 20).toFloat(), 860f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.style = Paint.Style.FILL
        canvas.drawText("No.", 40f, 830f, paint)
        canvas.drawText("Nama", 150f, 830f, paint)
        canvas.drawText("Datang", 700f, 830f, paint)
        canvas.drawText("Ket.", 1050f, 830f, paint)
        canvas.drawLine(130f, 790f, 130f, 840f, paint)
        canvas.drawLine(680f, 790f, 680f, 840f, paint)
        canvas.drawLine(1030f, 790f, 1030f, 840f, paint)

        var y = 950
        for (i in 1..4) {
            canvas.drawText("$i.", 40f, y.toFloat(), paint)
            canvas.drawText("Sendy", 150f, y.toFloat(), paint)
            canvas.drawText("08:15:00", 700f, y.toFloat(), paint)
            canvas.drawText("--", 1100f, y.toFloat(), paint)

            y += 100
        }

        pdfDocument.finishPage(page)

        val file = File(Environment.getExternalStorageDirectory(), "/sales_laporan.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        pdfDocument.close()
        Toast.makeText(this, "Periksa file manager anda", Toast.LENGTH_LONG).show()

    }
}
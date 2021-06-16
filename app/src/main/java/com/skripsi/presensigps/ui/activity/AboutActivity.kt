package com.skripsi.presensigps.ui.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.network.ApiClient
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class AboutActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 1
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var thumbNail: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar?.title = "Tentang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        registerCamera()

        btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(intent)

//                if (thumbNail != null) {
//                    val imgString = convertToString()
//                    presence("20", imgString)
//                }

            } else {
                requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
                )
            }
        }
    }

    private fun presence(idUser: String, imgString: String) {

        ApiClient.instance.addPresence(idUser, imgString)
            .enqueue(object : Callback<DataResponse> {
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    val value = response.body()?.value
                    val message = response.body()?.message

                    if (value.equals("1")) {
                        Toast.makeText(this@AboutActivity, message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this@AboutActivity, message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    Toast.makeText(this@AboutActivity, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }


    private fun registerCamera() {
        // open camera
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    thumbNail = data?.extras?.get("data") as Bitmap
                    imgThumbNail.setImageBitmap(thumbNail)
                }
            }
    }

    private fun convertToString(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        thumbNail?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imgByte = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imgByte, Base64.DEFAULT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (thumbNail != null) {
            val imgString = convertToString()
            presence("20", imgString)

            Log.e( "onResume: ", imgString)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
package com.skripsi.presensigps.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.network.ApiClient
import com.skripsi.presensigps.utils.Constant
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.activity_maps_info.*
import kotlinx.android.synthetic.main.activity_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var snackbar: Snackbar
    private val cameraPermissionCode = 1
    private val galleryPermissionCode = 11
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var thumbNail: Bitmap? = null
    private var uri: Uri? = null
    private lateinit var typeImg: String
    private lateinit var imgString: String

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.title = "Profil"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPref = PreferencesHelper(this)

        Glide.with(this@ProfileActivity)
            .load(sharedPref.getString(Constant.PREF_USER_IMG))
            .into(imgProfile)
        profileName.setText(sharedPref.getString(Constant.PREF_USER_NAME))
        profilePosition.setText(sharedPref.getString(Constant.PREF_USER_POSITION))
        profileEmail.setText(sharedPref.getString(Constant.PREF_USER_EMAIL))
        profilePassword.setText(sharedPref.getString(Constant.PREF_USER_PASSWORD))

        imgEdit.setOnClickListener {
            optionAlert()
        }

        btnLogout.setOnClickListener {

            if (sharedPref.getString(Constant.PREF_USER_POSITION) == "admin"
                || sharedPref.getString(Constant.PREF_USER_POSITION) == "manager"
                || sharedPref.getString(Constant.PREF_USER_POSITION) == "sales"
            ) {
                sharedPref.logout()
                finish()
            } else {
                sharedPref.logout()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
        }

    }

    private fun upImg(id: String?, imgString: String) {
        ApiClient.instance.upImg(id!!, imgString).enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                val message = response.body()?.message

                if (value.equals("1")) {
                    Glide.with(this@ProfileActivity)
                        .load(response.body()?.img)
                        .into(imgProfile)

                    sharedPref.put(Constant.PREF_USER_IMG, response.body()?.img.toString())

                } else {
                    snackbar =
                        Snackbar.make(
                            parentMapsInfoActivity,
                            message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                    snackbar.show()
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                snackbar =
                    Snackbar.make(
                        parentMapsInfoActivity,
                        t.message.toString(),
                        Snackbar.LENGTH_SHORT
                    )
                snackbar.show()

            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun optionAlert() {
        val options = arrayOf("Kamera", "Galeri")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Aksi")
        builder.setItems(
            options
        ) { _, which ->
            when (which) {
                0 -> {
                    typeImg = "camera"
                    openCamera()
                }
                1 -> {
                    typeImg = "gallery"
                    openGallery()
                }
            }
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(intent)

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA), cameraPermissionCode
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA), galleryPermissionCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == cameraPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(intent)
            }
        }

        if (requestCode == galleryPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher.launch(intent)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStart() {
        super.onStart()
        // register
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data

                    if (typeImg == "camera") {
                        thumbNail = data?.extras?.get("data") as? Bitmap
                    } else if (typeImg == "gallery") {
                        uri = data?.data
                        val source = ImageDecoder.createSource(this.contentResolver, uri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        thumbNail = bitmap
                    }
                }
            }
    }

    private fun convertToString(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        thumbNail?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imgByte = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imgByte, Base64.DEFAULT)
    }

    override fun onResume() {
        super.onResume()
        if (thumbNail != null) {
            imgString = convertToString()
            upImg(sharedPref.getString(Constant.PREF_USER_ID), imgString)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
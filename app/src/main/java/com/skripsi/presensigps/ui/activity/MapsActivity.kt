@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.network.ApiClient
import com.skripsi.presensigps.utils.Constant
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var sharedPref: PreferencesHelper

    private val tag = "MapsActivity()"
    private lateinit var snackbar: Snackbar
    private lateinit var progressDialog: ProgressDialog
    private lateinit var progressDialogPresence: ProgressDialog

    private val locationRequestCode = 1001
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationResult: LocationResult

    private var latLngList = ArrayList<LatLng>()
    private var locationNameList = ArrayList<String>()

    private var myLocLatitude: Double? = null
    private var myLocLongitude: Double? = null
    private lateinit var updateLatLng: LatLng
    private var latOffice: Double? = null
    private var longOffice: Double? = null
    private var radius: Double = 0.0
    private var distance: Float = 0.1f
    private var cameraZoom: Boolean = false

    private val cameraPermissionCode = 1
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var thumbNail: Bitmap? = null

    private var typeOnCLick: String? = null
    private var getInputLocationName: String? = null
    private var getMyLocLatitude: String? = null
    private var getMyLocLongitude: String? = null
    private var getInputNotes: String? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResultCallback: LocationResult) {
            progressDialog.dismiss()
            locationResult = locationResultCallback

            myLocLatitude = locationResult.locations[0].latitude
            myLocLongitude = locationResult.locations[0].longitude
            tvMyLatitude.text = myLocLatitude.toString()
            tvMyLongitude.text = myLocLongitude.toString()

            Log.e(
                tag,
                "onLocationResult: latitude = $myLocLatitude" +
                        " \n longitud0 = $myLocLongitude"
            )

            btnPresent.setBackgroundColor(Color.GRAY)
            if (distance <= radius) {
                Log.e(
                    tag,
                    "onLocationResult: $distance <= $radius"
                )
                btnPresent.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.dark_green
                    )
                )
            } else {
                Log.e(
                    tag,
                    "onLocationResult: $distance <= $radius"
                )
                btnPresent.setBackgroundColor(Color.GRAY)
            }

            setLatLng(
                myLocLatitude!!, myLocLongitude!!
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        sharedPref = PreferencesHelper(this)
        if (!sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        getUpdateLatLng()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Memuat Lokasi...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        btnOk.setOnClickListener {
            cardDialog.visibility = View.INVISIBLE
            btnReport.visibility = View.VISIBLE
            btnPresent.visibility = View.VISIBLE
        }

        btnSendReport.setOnClickListener {
            typeOnCLick = "report"
            getInputLocationName = inputLocation.text.toString()
            getMyLocLatitude = tvMyLatitude.text.toString()
            getMyLocLongitude = tvMyLongitude.text.toString()
            getInputNotes = inputNotes.text.toString()

            btnReport.visibility = View.VISIBLE
            btnPresent.visibility = View.VISIBLE
            when {
                getInputLocationName == "" -> inputLocation.error = "Tidak boleh kosong"
                getInputNotes == "" -> inputNotes.error = "Tidak boleh kosong"
                else -> openCamera()
            }

        }

        btnCancel.setOnClickListener {
            cardReport.visibility = View.INVISIBLE
            btnReport.visibility = View.VISIBLE
            btnPresent.visibility = View.VISIBLE
        }

        btnReport.setOnClickListener {
            btnReport.visibility = View.INVISIBLE
            btnPresent.visibility = View.INVISIBLE
            cardReport.visibility = View.VISIBLE
            cardReport.animation =
                AnimationUtils.loadAnimation(this@MapsActivity, R.anim.load)
            tvMyLatitude.text = myLocLatitude.toString()
            tvMyLongitude.text = myLocLongitude.toString()
        }

        btnPresent.setOnClickListener {
            typeOnCLick = "presence"

            if (distance <= radius) {

                btnPresent.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_green))
                openCamera()

            } else {
                btnPresent.setBackgroundColor(Color.GRAY)
                snackbar = Snackbar.make(
                    parentMapsActivity,
                    "Anda Tidak Masuk Di Area Kantor,\n" +
                            "Maksimal Jarak 50 Meter",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }
        }
    }

    private fun setLatLng(latitude: Double, longitude: Double) {

        latOffice = sharedPref.getString(Constant.PREF_OFFICE_LATITUDE)?.toDouble()
        longOffice = sharedPref.getString(Constant.PREF_OFFICE_LONGITUDE)?.toDouble()
        radius = sharedPref.getString(Constant.PREF_OFFICE_RADIUS)!!.toDouble()

        val myLocation = LatLng(latitude, longitude)
        val officeLocation = LatLng(latOffice!!, longOffice!!)

        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCameraPosition(
            CameraPosition.builder().target(LatLng(latitude, longitude)).zoom(19f).build()
        )

        if (!cameraZoom) mMap.animateCamera(cameraUpdate)
        cameraZoom = true

        mMap.clear()
        mMap.addMarker(MarkerOptions().position(myLocation).title("Lokasi Saya"))

        var whileLoop: Boolean
        var iName = 0
        for (i in latLngList) {
            updateLatLng = LatLng(i.latitude, i.longitude)

            whileLoop = true
            while (whileLoop) {
                mMap.addMarker(
                    MarkerOptions().position(updateLatLng).title(locationNameList[iName])
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                        )
                )
                iName += 1
                whileLoop = false
            }
        }

        val circleOptions = CircleOptions()
        circleOptions.center(officeLocation)
        circleOptions.radius(radius)
        circleOptions.fillColor(0x30ff0000)
        circleOptions.strokeWidth(5f)
        circleOptions.strokeColor(Color.GRAY)

        mMap.addCircle(circleOptions)

        val officeLoc = Location("Office Location")
        officeLoc.latitude = latOffice!!
        officeLoc.longitude = longOffice!!

        val myLoc = Location("My Location")
        myLoc.latitude = latitude
        myLoc.longitude = longitude

        distance = officeLoc.distanceTo(myLoc)
    }

    private fun presence(idUser: String, imgString: String) {
        progressDialogPresence = ProgressDialog(this)
        progressDialogPresence.setTitle("Loading")
        progressDialogPresence.setMessage("Mengirim Presensi...")
        progressDialogPresence.setCancelable(false)
        progressDialogPresence.show()

        ApiClient.instance.addPresence(idUser, imgString)
            .enqueue(object : Callback<DataResponse> {
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    val value = response.body()?.value
                    val message = response.body()?.message

                    if (value.equals("1")) {
                        progressDialogPresence.dismiss()

                        imgStatus.setImageResource(R.drawable.ic_success)
                        tvStatus.text = applicationContext.getString(R.string.success)
                        tvKetStatus.text = applicationContext.getString(R.string.presensi_berhasil)
                        btnReport.visibility = View.INVISIBLE
                        btnPresent.visibility = View.INVISIBLE
                        cardDialog.visibility = View.VISIBLE
                        cardDialog.animation =
                            AnimationUtils.loadAnimation(this@MapsActivity, R.anim.load)

                    } else {
                        progressDialogPresence.dismiss()

                        snackbar = Snackbar.make(
                            parentMapsActivity,
                            message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.show()
                    }

                    thumbNail = null
                }

                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    progressDialogPresence.dismiss()

                    imgStatus.setImageResource(R.drawable.ic_failed)
                    tvStatus.text = applicationContext.getString(R.string.gagal)
                    tvKetStatus.text = applicationContext.getString(R.string.presensi_gagal)
                    btnReport.visibility = View.INVISIBLE
                    btnPresent.visibility = View.INVISIBLE
                    cardDialog.visibility = View.VISIBLE
                    cardDialog.animation =
                        AnimationUtils.loadAnimation(this@MapsActivity, R.anim.load)

                    thumbNail = null
                }

            })
    }

    private fun getUpdateLatLng() {
        GlobalScope.launch(context = Dispatchers.Main) {

            ApiClient.instance.getUpdateLatlngReport(sharedPref.getString(Constant.PREF_USER_ID)!!)
                .enqueue(object : Callback<DataResponse> {
                    override fun onResponse(
                        call: Call<DataResponse>,
                        response: Response<DataResponse>
                    ) {
                        val value = response.body()?.value
                        var message = "Update"

                        if (value.equals("1")) {
                            latLngList.clear()
                            locationNameList.clear()

                            for (i in response.body()!!.result) {
                                latLngList.add(
                                    LatLng(
                                        i.latitude.toDouble(),
                                        i.longitude.toDouble()
                                    )
                                )
                                updateLatLng = LatLng(i.latitude.toDouble(), i.longitude.toDouble())
                                mMap.addMarker(
                                    MarkerOptions().position(updateLatLng).title(i.location_name)
                                        .icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_GREEN
                                            )
                                        )
                                )

                                locationNameList.add(i.location_name)
                            }
                            snackbar = Snackbar.make(
                                parentMapsActivity,
                                message,
                                Snackbar.LENGTH_SHORT
                            )
                            snackbar.show()
                        } else {
                            message = "Gagal Update"
                            snackbar = Snackbar.make(
                                parentMapsActivity,
                                message,
                                Snackbar.LENGTH_SHORT
                            )
                            snackbar.show()
                        }
                    }

                    override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                        snackbar = Snackbar.make(
                            parentMapsActivity,
                            t.message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.show()
                    }
                })
        }
    }

    private fun report(
        idUser: String,
        getInputLocationName: String,
        getMyLocLatitude: String,
        getMyLocLongitude: String,
        getInputNotes: String,
        imgString: String
    ) {
        progressDialogPresence = ProgressDialog(this)
        progressDialogPresence.setTitle("Loading")
        progressDialogPresence.setMessage("Mengirim Laporan...")
        progressDialogPresence.setCancelable(false)

        ApiClient.instance.addReport(
            idUser,
            getInputLocationName,
            getMyLocLatitude,
            getMyLocLongitude,
            imgString,
            getInputNotes
        ).enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                val message = response.body()?.message

                if (value.equals("1")) {
                    cardReport.visibility = View.INVISIBLE
                    progressDialogPresence.dismiss()

                    getUpdateLatLng()

                    imgStatus.setImageResource(R.drawable.ic_success)
                    tvStatus.text = applicationContext.getString(R.string.success)
                    tvKetStatus.text = applicationContext.getString(R.string.report_berhasil)
                    btnReport.visibility = View.INVISIBLE
                    btnPresent.visibility = View.INVISIBLE
                    cardDialog.visibility = View.VISIBLE
                    cardDialog.animation =
                        AnimationUtils.loadAnimation(this@MapsActivity, R.anim.load)

                    inputLocation.setText("")
                    inputNotes.setText("")

                } else {
                    cardReport.visibility = View.INVISIBLE
                    progressDialogPresence.dismiss()

                    snackbar = Snackbar.make(
                        parentMapsActivity,
                        message.toString(),
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar.show()
                }

                thumbNail = null
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                cardReport.visibility = View.INVISIBLE
                progressDialogPresence.dismiss()

                imgStatus.setImageResource(R.drawable.ic_failed)
                tvStatus.text = applicationContext.getString(R.string.gagal)
                tvKetStatus.text = applicationContext.getString(R.string.report_gagal)
                btnReport.visibility = View.INVISIBLE
                btnPresent.visibility = View.INVISIBLE
                cardDialog.visibility = View.VISIBLE
                cardDialog.animation =
                    AnimationUtils.loadAnimation(this@MapsActivity, R.anim.load)

                thumbNail = null
            }
        })
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

    private fun convertToString(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        thumbNail?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imgByte = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imgByte, Base64.DEFAULT)
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun askLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                locationRequestCode
            )
        } else {
            Toast.makeText(this, "tidak", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                locationRequestCode
            )
        }
    }

    private fun checkSettingAndStartLocationUpdates() {
        val request: LocationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val client: SettingsClient = LocationServices.getSettingsClient(this)

        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(request)

        task.addOnSuccessListener {
            startLocationUpdates()
        }

        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                val apiException: ResolvableApiException = it
                try {
                    apiException.startResolutionForResult(this, locationRequestCode)
                    askLocationPermission()
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettingAndStartLocationUpdates()
            }
        }

        if (requestCode == cameraPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(intent)
            }
        }
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

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkSettingAndStartLocationUpdates()

        } else {
            askLocationPermission()
        }

        // register camera
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    thumbNail = data?.extras?.get("data") as Bitmap
                }
            }
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (!sharedPref.getBoolean(Constant.PREF_IS_LOGIN)
        ) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        } else if (!sharedPref.getString(Constant.PREF_USER_POSITION)
                .equals("sales")
        ) {
            finish()
        } else {
            if (thumbNail != null) {
                val imgString = convertToString()

                when (typeOnCLick) {

                    "presence" -> presence(sharedPref.getString(Constant.PREF_USER_ID)!!, imgString)
                    "report" -> {
                        report(
                            sharedPref.getString(Constant.PREF_USER_ID)!!,
                            getInputLocationName!!,
                            getMyLocLatitude!!,
                            getMyLocLongitude!!,
                            getInputNotes!!,
                            imgString
                        )

                    }

                }

                Log.e("onResume: ", imgString)
            }
        }

    }

}
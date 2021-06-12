package com.skripsi.presensigps.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val tag = "MapsActivity()"
    private lateinit var snackbar: Snackbar
    private val locationRequestCode = 1001
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationResult: LocationResult

    private val latOffice: Double = -5.098802113912516
    private val longOffice = 119.5342535418403
    private val radius = 50.0
    private var distance: Float = 50.1f

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResultCallback: LocationResult) {
            locationResult = locationResultCallback

            Log.e(
                tag,
                "onLocationResult: latitude = ${locationResult.locations[0].latitude}" +
                        " \n longitud0 = ${locationResult.locations[0].longitude}"
            )

            btnHadir.setBackgroundColor(Color.GRAY)
            if (distance <= radius) {
                Log.e(
                    tag,
                    "onLocationResult: $distance <= $radius"
                )
                btnHadir.setBackgroundColor(
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
                btnHadir.setBackgroundColor(Color.GRAY)
            }

            setLatLng(
                locationResult.locations[0].latitude,
                locationResult.locations[0].longitude
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        btnHadir.setOnClickListener {
            if (distance <= radius) {
                btnHadir.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_green))
                snackbar = Snackbar.make(
                    parentMapsActivity,
                    "Anda Masuk Diarea",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            } else {
                btnHadir.setBackgroundColor(Color.GRAY)
                snackbar = Snackbar.make(
                    parentMapsActivity,
                    "Anda Tidak Masuk Diarea,\n" +
                            "Jarak Kantor $distance Meter",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }
        }
    }

    private fun setLatLng(latitude: Double, longitude: Double) {

        val myLocation = LatLng(latitude, longitude)
        val officeLocation = LatLng(latOffice, longOffice)

        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCameraPosition(
            CameraPosition.builder().target(LatLng(latitude, longitude)).zoom(19f).build()
        )

        mMap.animateCamera(cameraUpdate)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(myLocation).title("My Location"))

        val circleOptions = CircleOptions()
        circleOptions.center(officeLocation)
        circleOptions.radius(radius)
        circleOptions.fillColor(0x30ff0000)
        circleOptions.strokeWidth(5f)
        circleOptions.strokeColor(Color.GRAY)

        mMap.addCircle(circleOptions)

        val officeLoc = Location("Office Location")
        officeLoc.latitude = latOffice
        officeLoc.longitude = longOffice

        val myLoc = Location("My Location")
        myLoc.latitude = latitude
        myLoc.longitude = longitude

        distance = officeLoc.distanceTo(myLoc)

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
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
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
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                locationRequestCode
            )
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

    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }
}
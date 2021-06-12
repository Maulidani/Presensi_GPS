package com.skripsi.presensigps.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.skripsi.presensigps.R
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getDeviceLocation() {
        //checkPermission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationProviderClient.lastLocation.addOnCompleteListener {
                val location = it.result
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())

                    try {
                        val address: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        setLatLng(address[0].latitude, address[0].longitude)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } else {

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Alert")
                    builder.setMessage("Aktifkan Lokasi Anda terlebih dahulu")
                    builder.setCancelable(false)

                    builder.setPositiveButton("Ok") { _, _ ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }

                    builder.setNegativeButton("Cancel") { _, _ ->
                    }

                    builder.show()
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                44
            )
        }
    }

    private fun setLatLng(latitude: Double, longitude: Double) {

        val latOffice: Double = -5.185939215081544
        val longOffice = 119.41443375306726
        val radius = 50.0

        val myLocation = LatLng(latitude, longitude)
        val officeLocation = LatLng(latOffice, longOffice)
        val circleOptions = CircleOptions()
        mMap.addMarker(MarkerOptions().position(myLocation).title("My Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 19.5f))

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

        val distance = officeLoc.distanceTo(myLoc)

        if (distance <= 30f) {
            Toast.makeText(this, "Anda Masuk Diarea $distance ", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Anda Tidak Masuk Diarea $distance ", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()

        getDeviceLocation()
    }
}
@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.ui.activity

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.network.ApiClient
import kotlinx.android.synthetic.main.activity_maps_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsInfoActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var snackbar: Snackbar
    private lateinit var progressDialog: ProgressDialog

    //    private var latLngList = ArrayList<LatLng>()
    //    private var locationNameList = ArrayList<String>()
    private lateinit var latLngLocation: LatLng
    private lateinit var cameraUpdate: CameraUpdate

    private var check: Boolean? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private var name: String? = null
    private var status: String? = null
    private var location: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_info)
        supportActionBar?.title = "Laporan Hari Ini"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        check = intent.getBooleanExtra("cek", false)
        latitude = intent.getStringExtra("latitude").toString()
        longitude = intent.getStringExtra("longitude").toString()
        name = intent.getStringExtra("name").toString()
        status = intent.getStringExtra("status").toString()
        location = intent.getStringExtra("location").toString()
        val img = intent.getStringExtra("img").toString()
        val date = intent.getStringExtra("date").toString()
        val time = intent.getStringExtra("time").toString()
        val notes = intent.getStringExtra("notes").toString()

        if (check == true) {
            parentDetails.visibility = View.VISIBLE
            Glide.with(this)
                .load(img)
                .into(imgReport)
            tvName.text = name
            tvLocation.text = location
            tvLatitude.text = latitude
            tvLongitude.text = longitude
            tvDate.text = date
            tvTime.text = time
            inputNotes.setText(notes)
            if (status == "1") {
                btnVerifikasi.text = getString(R.string.telah_diverifikasi)
                btnVerifikasi.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.dark_green
                    )
                )
            } else {
                btnVerifikasi.text = getString(R.string.verifikasi)
                btnVerifikasi.setTextColor(Color.BLACK)
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapInfo) as SupportMapFragment
        mapFragment.getMapAsync(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Memuat Lokasi...")
        progressDialog.setCancelable(false)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val makassar = LatLng(-5.135845591957834, 119.45307934371301)
        cameraUpdate = CameraUpdateFactory.newCameraPosition(
            CameraPosition.builder().target(makassar).zoom(11f)
                .build()
        )
        mMap.animateCamera(cameraUpdate)

        if (check == true) {
            latLngLocation = LatLng(latitude!!.toDouble(), longitude!!.toDouble())
            cameraUpdate = CameraUpdateFactory.newCameraPosition(
                CameraPosition.builder().target(latLngLocation)
                    .zoom(19f)
                    .build()
            )
            if (status == "1") {
                mMap.addMarker(
                    MarkerOptions().position(latLngLocation).title(name)
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                        )
                )
            } else {
                mMap.addMarker(
                    MarkerOptions().position(latLngLocation).title(name)
                )
            }
            mMap.animateCamera(cameraUpdate)

        } else {
            getReportLatLnt()
        }
    }

    private fun getReportLatLnt() {
        progressDialog.show()

        ApiClient.instance.getReport("today").enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val result = response.body()?.result
                val value = response.body()?.value
                var message = "Belum ada laporan hari ini"

                if (value.equals("1")) {

                    if (result.isNullOrEmpty()) {
                        snackbar =
                            Snackbar.make(parentMapsInfoActivity, message, Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }

                    for (i in result!!) {
//                        latLngList.add(
//                            LatLng(
//                                i.latitude.toDouble(),
//                                i.longitude.toDouble()
//                            )
//                        )
//                        locationNameList.add(i.location_name)
                        latLngLocation = LatLng(i.latitude.toDouble(), i.longitude.toDouble())
                        if (i.status == "1") {
                            mMap.addMarker(
                                MarkerOptions().position(latLngLocation).title(i.name)
                                    .icon(
                                        BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_GREEN
                                        )
                                    )
                            )
                        } else {
                            mMap.addMarker(
                                MarkerOptions().position(latLngLocation).title(i.name)
                            )
                        }
                    }
                } else {
                    message = "Gagal"
                    snackbar =
                        Snackbar.make(parentMapsInfoActivity, message, Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                progressDialog.dismiss()

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
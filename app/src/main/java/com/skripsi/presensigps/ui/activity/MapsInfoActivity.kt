@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.ui.activity

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.network.ApiClient
import kotlinx.android.synthetic.main.activity_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsInfoActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var snackbar: Snackbar
    private lateinit var progressDialog: ProgressDialog
    private var latLngList = ArrayList<LatLng>()
    private var locationNameList = ArrayList<String>()
    private lateinit var LatLngLocation: LatLng
    private lateinit var cameraUpdate: CameraUpdate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_info)

        supportActionBar?.title = "Laporan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        val check: Boolean = intent.getBooleanExtra("cek", false)
        val latitude = intent.getStringExtra("latitude").toString()
        val longitude = intent.getStringExtra("longitude").toString()
        val name = intent.getStringExtra("name").toString()
        val status = intent.getStringExtra("status").toString()

        if (check) {
            LatLngLocation = LatLng(latitude.toDouble(), longitude.toDouble())
            cameraUpdate = CameraUpdateFactory.newCameraPosition(
                CameraPosition.builder().target(LatLngLocation)
                    .zoom(19f)
                    .build()
            )
            if (status == "1") {
                mMap.addMarker(
                    MarkerOptions().position(LatLngLocation).title(name)
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                        )
                )
            } else {
                mMap.addMarker(
                    MarkerOptions().position(LatLngLocation).title(name)
                )
            }
            mMap.animateCamera(cameraUpdate)

        } else {
            getReportLatLnt()
        }
    }

    private fun getReportLatLnt() {
        progressDialog.show()

        ApiClient.instance.getReport().enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                var message = "Sukses"

                if (value.equals("1")) {

                    for (i in response.body()!!.result) {
//                        latLngList.add(
//                            LatLng(
//                                i.latitude.toDouble(),
//                                i.longitude.toDouble()
//                            )
//                        )
//                        locationNameList.add(i.location_name)
                        LatLngLocation = LatLng(i.latitude.toDouble(), i.longitude.toDouble())
                        if (i.status == "1") {
                            mMap.addMarker(
                                MarkerOptions().position(LatLngLocation).title(i.name)
                                    .icon(
                                        BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_GREEN
                                        )
                                    )
                            )
                        } else {
                            mMap.addMarker(
                                MarkerOptions().position(LatLngLocation).title(i.name)
                            )
                        }
                    }
                } else {
                    message = "Gagal"
                }
                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                progressDialog.dismiss()

                snackbar =
                    Snackbar.make(parentInfoActivity, t.message.toString(), Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
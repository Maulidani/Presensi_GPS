package com.skripsi.presensigps.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
//    private const val URL = "https://lapilaboratories.tryapp.my.id/"
    private const val URL = "http://192.168.186.5/presensi_gps_new/presensi_gps/"

//    var gson = GsonBuilder()
//        .setLenient()
//        .create()
//
    val instance: ApiInterface by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiInterface::class.java)
    }

}
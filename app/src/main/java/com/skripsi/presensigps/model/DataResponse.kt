package com.skripsi.presensigps.model

data class DataResponse(
    val value: String,
    val message: String,

    //response login
    val id: String,
//    val name: String,
    val position: String,
    val email: String,
    val password: String,

    //response offile location
    val name: String, // login
    val latitude: String,
    val longitude: String,
    val radius: String,

    //response position/jabatan
    val result: ArrayList<ResultPosition>

)

data class ResultPosition(
    val position: String
)
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

    //response Presence
    val result: ArrayList<Result>,

    //info admin
    val total_presence: String,
    val total_report: String,
    val total_user: String,
    val time: String

)

data class Result(
    val id: String,
    val office: String,
    val name: String,
    val location_name: String,
    val latitude: String,
    val longitude: String,
    val notes: String,
    val img: String,
    val date: String,
    val time: String,
    val status: String,

    var expendable: Boolean = false
)
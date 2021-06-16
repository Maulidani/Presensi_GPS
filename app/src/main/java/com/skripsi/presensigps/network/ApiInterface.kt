package com.skripsi.presensigps.network

import com.skripsi.presensigps.model.DataResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<DataResponse>

    @FormUrlEncoded
    @POST("register.php")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("position") position: String
    ): Call<DataResponse>

    @FormUrlEncoded
//    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("presence.php")
    fun addPresence(
        @Field("name") idName: String,
        @Field("img") img: String
    ): Call<DataResponse>

    @FormUrlEncoded
    @POST("presence_verification.php")
    fun verificationPresence(
        @Field("id") id: String,
        @Field("status") status: String
    ): Call<DataResponse>

//    @GET("get_position.php")
//    fun getPosition(): Call<DataResponse>

    @GET("office_location.php")
    fun getOfficeLocation(): Call<DataResponse>

    @GET("get_presence.php")
    fun getPresence(): Call<DataResponse>

}
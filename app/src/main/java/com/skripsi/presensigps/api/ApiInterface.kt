package com.skripsi.presensigps.api

import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.model.ResultPosition
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

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
    @POST("presence.php")
    fun addPresence(
        @Field("name") idName: String
    ): Call<DataResponse>

    @GET("get_position.php")
    fun getPosition(): Call<DataResponse>

    @GET("office_location.php")
    fun getOfficeLocation(): Call<DataResponse>

}
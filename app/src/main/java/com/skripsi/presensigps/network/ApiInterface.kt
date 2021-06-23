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
//    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("sales_report.php")
    fun addReport(
        @Field("name") idName: String,
        @Field("location_name") locName: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("img") img: String,
        @Field("notes") notes: String
    ): Call<DataResponse>

    @FormUrlEncoded
    @POST("presence_verification.php")
    fun verificationPresence(
        @Field("id") id: String,
        @Field("status") status: String
    ): Call<DataResponse>

    @FormUrlEncoded
    @POST("report_verification.php")
    fun verificationReport(
        @Field("id") id: String,
        @Field("status") status: String
    ): Call<DataResponse>

//    @GET("get_position.php")
//    fun getPosition(): Call<DataResponse>

    @GET("office_location.php")
    fun getOfficeLocation(): Call<DataResponse>

    @GET("get_presence.php")
    fun getPresence(): Call<DataResponse>

    @GET("get_sales_report.php")
    fun getReport(): Call<DataResponse>

    @GET("get_update_latlng.php")
    fun getUpdateLatlngReport(
        @Query("name") name: String
    ): Call<DataResponse>

    @GET("get_info_admin.php")
    fun getInfoAdmin(): Call<DataResponse>

}
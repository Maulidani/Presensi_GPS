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
    @POST("verification.php")
    fun verification(
        @Field("id") id: String,
        @Field("status") status: String,
        @Field("type") type: String
    ): Call<DataResponse>

    @FormUrlEncoded
    @POST("edit_user.php")
    fun editUser(
        @Field("id") idName: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("position") position: String
    ): Call<DataResponse>

    @FormUrlEncoded
    @POST("cancel_verification.php")
    fun cancelVerification(
        @Field("id") id: String,
        @Field("status") status: String,
        @Field("type") type: String
    ): Call<DataResponse>

    @GET("office_location.php")
    fun getOfficeLocation(): Call<DataResponse>

    //    @GET("get_presence.php")
//    fun getPresence(): Call<DataResponse>

    @FormUrlEncoded
    @POST("presence_back.php")
    fun presenceBack(
        @Field("id") id: String
    ): Call<DataResponse>

    @GET("get_sales_report.php")
    fun getReport(
        @Query("today") today: String
    ): Call<DataResponse>

    @GET("get_update_latlng.php")
    fun getUpdateLatlngReport(
        @Query("name") name: String
    ): Call<DataResponse>

    @GET("get_info_admin.php")
    fun getInfoAdmin(): Call<DataResponse>

    @GET("get_user.php")
    fun getUser(
        @Query("id") id: String,
        @Query("position") position: String
    ): Call<DataResponse>

    @GET("get_detail_presence.php")
    fun getDetailPresence(
        @Query("name") id: String
    ): Call<DataResponse>

    @GET("get_report_for_pdf.php")
    fun getReportPDF(
        @Query("when") sWhen: String,
        @Query("year") sYear: String
    ): Call<DataResponse>

    @FormUrlEncoded
    @POST("delete.php")
    fun delete(
        @Field("id") id: String,
        @Field("type") type: String
    ): Call<DataResponse>

    @FormUrlEncoded
    @POST("up_img_user.php")
    fun upImg(
        @Field("id") id: String,
        @Field("img") img: String
    ): Call<DataResponse>
}
package com.slings.vasantham.service

import com.slings.vasantham.model.AttendanceInModel
import com.slings.vasantham.model.AttendanceLogModel
import com.slings.vasantham.model.AttendanceMonthModel
import com.slings.vasantham.model.CommonModel
import com.slings.vasantham.model.IsLoginModel
import com.slings.vasantham.model.LoginModel
import com.slings.vasantham.model.PermissionModel
import com.slings.vasantham.model.PermissionStatusModel
import com.slings.vasantham.model.ProfileModel
import com.slings.vasantham.model.ShiftTimingsModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File
import java.util.concurrent.TimeUnit


interface RetrofitService {
    @POST("login")
    suspend fun login(
        @Query("mobile") mobile: String,
        @Query("password") password: String,
        @Query("deviceInfo") s: String
    ): Response<LoginModel>

    @POST("staff/getshift/{userId}")
    suspend fun getshift(
        @Path("userId") userId: Int
    ): Response<ShiftTimingsModel>


    @POST("staff/islogin/{userId}")
    suspend fun islogin(
        @Path("userId") userId: String
    ): Response<IsLoginModel>

    @Multipart
    @POST("staff/attendance/in")
    suspend fun attendanceIn(
        @Part("userId") userId: String,
        @Part("shiftId") shiftId: String,
        @Part("associatedId") associatedId: String,
        @Part("imageUrl") imageUrl: File
    ): Response<AttendanceInModel>


    @POST("staff/attendance/out")
    suspend fun attendanceOut(
        @Query("userId") userId: String
    ): Response<CommonModel>


    @POST("staff/attendance/dayLog")
    suspend fun attendanceDayLog(
        @Query("userId") userId: String,
        @Query("date") date: String
    ): Response<AttendanceLogModel>

    @GET("staff/permissionStatus/{userId}")
    suspend fun permissionStatus(
        @Path("userId") userId: String,
        @Query("userId") userid: String
    ): Response<PermissionStatusModel>

    @POST("staff/permission/{userId}")
    suspend fun permissionApplyIn(
        @Path("userId") userId: String,
        @Query("imageUrl") date: String,
        @Query("reason") reason: String
    ): Response<PermissionModel>

    @POST("staff/permission/{userId}")
    suspend fun permissionApplyOut(
        @Path("userId") userId: String,
    ): Response<PermissionModel>

    @GET("staff/attendance/monthlyLog/{userId}/{year}/{month}")
    suspend fun attendanceMonthLog(
        @Path("userId") userId: String,
        @Path("year") year: Int,
        @Path("month") month: Int,
    ): Response<AttendanceMonthModel>

    @GET("staff/profile/{userId}")
    suspend fun profileEditData (
        @Path("userId") userId: String,
    ): Response<ProfileModel>


    companion object {

        var retrofitService: RetrofitService? = null
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        var okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        fun getInstance(): RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://maduraivasantham.com/api/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }
    }

}

package com.slings.vasantham.model

import com.google.gson.annotations.SerializedName

data class AttendanceMonthModel(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: AttMonthLogData

)

data class AttMonthLogData(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("presents")
    val presents: List<String>,
    @SerializedName("permissions")
    val permissions: List<String>,
    @SerializedName("leaves")
    val leaves: List<String>,

    )

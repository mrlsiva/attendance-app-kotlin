package com.slings.vasantham.model

import com.google.gson.annotations.SerializedName

data class AttendanceLogModel(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: AttLogData

)

data class AttLogData(
    @SerializedName("checkInTime")
    val checkInTime: String,
    @SerializedName("checkOutTime")
    val checkOutTime: String,
    @SerializedName("actualCheckInTime")
    val actualCheckInTime: String,
    @SerializedName("actualCheckOutTime")
    val actualCheckOutTime: String,
    @SerializedName("permissionInHours")
    val permissionInHours: String,
    @SerializedName("punchOutBtnTime")
    val punchOutBtnTime: String,
)


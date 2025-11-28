package com.slings.vasantham.model

import com.google.gson.annotations.SerializedName

data class AttendanceInModel(

    @SerializedName("success") val success: Boolean,

    @SerializedName("data") val data: AttenInData,
)

data class AttenInData(
    @SerializedName("currentTime")
    val currentTime: String,
    @SerializedName("punchInTime")
    val punchInTime: String,
    @SerializedName("lateBy")
    val lateBy: String,
    @SerializedName("atStatus")
    val atStatus: String,
    @SerializedName("message")
    val message: String,
)


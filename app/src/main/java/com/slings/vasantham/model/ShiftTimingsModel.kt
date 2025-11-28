package com.slings.vasantham.model

import com.google.gson.annotations.SerializedName

data class ShiftTimingsModel(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: Data

)

data class Data(
    @SerializedName("shiftId")
    val shiftId: Int,
    @SerializedName("shiftName")
    val shiftName: String,
    @SerializedName("startTime")
    val startTime: String,
    @SerializedName("endTime")
    val endTime: String,
    @SerializedName("effectiveFrom")
    val effectiveFrom: String,
    @SerializedName("effectiveTo")
    val effectiveTo: String,
    @SerializedName("currentTime")
    val currentTime: String,
    @SerializedName("punchInBtnTime")
    val punchInBtnTime: String,
    @SerializedName("punchOutBtnTime")
    val punchOutBtnTime: String,
    @SerializedName("associatedId")
    val associatedId: Int,

    )

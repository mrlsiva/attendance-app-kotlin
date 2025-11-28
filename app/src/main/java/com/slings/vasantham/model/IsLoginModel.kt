package com.slings.vasantham.model

import com.google.gson.annotations.SerializedName

data class IsLoginModel(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: isLoginData

)

data class isLoginData(
    @SerializedName("atStatus")
    val atStatus: Int?,
    @SerializedName("currentTime")
    val currentTime: String?,
    @SerializedName("punchInBtnTime")
    val punchInBtnTime: String?,
    @SerializedName("punchInTime")
    val punchInTime: String?,
    @SerializedName("punchOutTime")
    val punchOutTime: String?,
    @SerializedName("lateBy")
    val lateBy: String?,

)

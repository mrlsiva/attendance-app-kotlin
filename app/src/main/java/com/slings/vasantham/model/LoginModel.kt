package com.slings.vasantham.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("success")
    val success: Success? = null,
    @SerializedName("error")
      val error: String? = "error"
)

data class Success(
    @SerializedName("userId")
    val userId: Int,
   @SerializedName("username")
    val username: String,
    @SerializedName("designationId")
    val designationId: Int,
    @SerializedName("designation")
    val designation: String,
    @SerializedName("shiftname")
    val shiftname: String,
    @SerializedName("startTime")
    val startTime: String,
    @SerializedName("endTime")
    val endTime: String,
    @SerializedName("currentTime")
    val currentTime: String,
    @SerializedName("punchInBtnTime")
    val punchInBtnTime: String,
    @SerializedName("punchOutBtnTime")
    val punchOutBtnTime: String,
    @SerializedName("token")
    val token: String
)

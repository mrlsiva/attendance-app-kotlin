package com.slings.vasantham.model

import com.google.gson.annotations.SerializedName

data class ProfileModel(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: ProfileData

)

data class ProfileData(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("email")
    val email: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("DOB")
    val DOB: String,

    @SerializedName("userImageUrl")
    val userImageUrl: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("roleId")
    val roleId: Int,
    @SerializedName("roleName")
    val roleName: String,
    @SerializedName("roleDescription")
    val roleDescription: String,
)

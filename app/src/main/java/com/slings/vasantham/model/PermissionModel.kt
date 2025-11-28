package com.slings.vasantham.model

import com.google.gson.annotations.SerializedName

data class PermissionModel(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?,

    @SerializedName("error")
    val error: Error?,

    @SerializedName("data")
    val data: PermissionData?,
)

data class PermissionData(
    @SerializedName("currentTime")
    val currentTime: String,
    @SerializedName("permissionStartTime")
    val permissionStartTime: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("permissionEndTime")
    val permissionEndTime: String,

)


data class PermissionStatusModel(
    @SerializedName("success")
    val success: Boolean?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: PermissionStatusData?,
)

data class PermissionStatusData(
    @SerializedName("atStatus")
    val atStatus: String,
    @SerializedName("permissionStartTime")
    val permissionStartTime: String,
    @SerializedName("permissionEndTime")
    val permissionEndTime: String,
)
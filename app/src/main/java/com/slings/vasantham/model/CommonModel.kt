package com.slings.vasantham.model

import com.google.gson.annotations.SerializedName

data class CommonModel(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String

)

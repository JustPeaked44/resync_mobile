package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @SerializedName("email")
    @Json(name = "email")
    val email: String,

    @SerializedName("password")
    @Json(name = "password")
    val password: String
)

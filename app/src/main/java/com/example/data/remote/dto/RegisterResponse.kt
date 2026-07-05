package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @SerializedName("token")
    @Json(name = "token")
    val token: String,

    @SerializedName("email")
    @Json(name = "email")
    val email: String,

    @SerializedName("name")
    @Json(name = "name")
    val name: String
)

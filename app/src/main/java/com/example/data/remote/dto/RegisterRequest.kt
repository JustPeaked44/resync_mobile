package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @SerializedName("name")
    @Json(name = "name")
    val name: String,

    @SerializedName("email")
    @Json(name = "email")
    val email: String,

    @SerializedName("password")
    @Json(name = "password")
    val password: String,

    @SerializedName("institution")
    @Json(name = "institution")
    val institution: String,

    @SerializedName("role")
    @Json(name = "role")
    val role: String
)

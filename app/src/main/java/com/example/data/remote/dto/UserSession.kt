package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserSession(
    @SerializedName("token")
    @Json(name = "token")
    val token: String = "",

    @SerializedName("email")
    @Json(name = "email")
    val email: String,

    @SerializedName("name")
    @Json(name = "name")
    val name: String,

    @SerializedName("institution")
    @Json(name = "institution")
    val institution: String? = null,

    @SerializedName("academic_role")
    @Json(name = "academic_role")
    val academicRole: String? = null,

    @SerializedName("bio")
    @Json(name = "bio")
    val bio: String? = null
)

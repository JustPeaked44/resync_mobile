package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExplanationDetail(
    @SerializedName("what_was_found")
    @Json(name = "what_was_found")
    val whatWasFound: String,

    @SerializedName("why_it_matters")
    @Json(name = "why_it_matters")
    val whyItMatters: String,

    @SerializedName("suggested_fix")
    @Json(name = "suggested_fix")
    val suggestedFix: String
)

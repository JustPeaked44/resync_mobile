package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SuggestionItem(
    @SerializedName("category")
    @Json(name = "category")
    val category: String,

    @SerializedName("issue_title")
    @Json(name = "issue_title")
    val issueTitle: String,

    @SerializedName("diagnostic_explanation")
    @Json(name = "diagnostic_explanation")
    val diagnosticExplanation: String,

    @SerializedName("remedy")
    @Json(name = "remedy")
    val remedy: String
)

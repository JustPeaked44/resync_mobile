package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScanRequest(
    @SerializedName("document_url")
    @Json(name = "document_url")
    val documentUrl: String,

    @SerializedName("chapter_category")
    @Json(name = "chapter_category")
    val chapterCategory: String = "Full Manuscript",

    @SerializedName("research_theme")
    @Json(name = "research_theme")
    val researchTheme: String? = null,

    @SerializedName("research_type")
    @Json(name = "research_type")
    val researchType: String? = null
)

package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScanReport(
    @SerializedName("coherence_score")
    @Json(name = "coherence_score")
    val coherenceScore: Int,

    @SerializedName("executive_summary")
    @Json(name = "executive_summary")
    val executiveSummary: String,

    @SerializedName("inconsistencies")
    @Json(name = "inconsistencies")
    val inconsistencies: List<InconsistencyItem>,

    @SerializedName("suggestions")
    @Json(name = "suggestions")
    val suggestions: List<SuggestionItem>,

    @SerializedName("references")
    @Json(name = "references")
    val references: List<CitationItem>,

    @SerializedName("created_at")
    @Json(name = "created_at")
    val createdAt: String
)

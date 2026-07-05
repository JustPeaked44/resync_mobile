package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScanResponse(
    @SerializedName("coherence_score")
    @Json(name = "coherence_score")
    val coherenceScore: Int,

    @SerializedName("overall_assessment")
    @Json(name = "overall_assessment")
    val overallAssessment: String,

    @SerializedName("inconsistencies")
    @Json(name = "inconsistencies")
    val inconsistencies: List<InconsistencyItem>,

    @SerializedName("references")
    @Json(name = "references")
    val references: List<CitationItem>,

    @SerializedName("duplicate_sections")
    @Json(name = "duplicate_sections")
    val duplicateSections: List<DuplicateItem> = emptyList(),

    @SerializedName("missing_sections")
    @Json(name = "missing_sections")
    val missingSections: List<String> = emptyList(),

    @SerializedName("created_at")
    @Json(name = "created_at")
    val createdAt: String,

    @SerializedName("manuscript_text")
    @Json(name = "manuscript_text")
    val manuscriptText: String? = null
)

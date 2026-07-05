package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class InconsistencyType {
    @SerializedName("contradiction")
    @Json(name = "contradiction")
    CONTRADICTION,

    @SerializedName("redundancy")
    @Json(name = "redundancy")
    REDUNDANCY,

    @SerializedName("logic_gap")
    @Json(name = "logic_gap")
    LOGIC_GAP,

    @SerializedName("terminology_clash")
    @Json(name = "terminology_clash")
    TERMINOLOGY_CLASH
}

enum class Severity {
    @SerializedName("High")
    @Json(name = "High")
    HIGH,

    @SerializedName("Medium")
    @Json(name = "Medium")
    MEDIUM,

    @SerializedName("Low")
    @Json(name = "Low")
    LOW
}

@JsonClass(generateAdapter = true)
data class InconsistencyItem(
    @SerializedName("type")
    @Json(name = "type")
    val type: InconsistencyType,

    @SerializedName("severity")
    @Json(name = "severity")
    val severity: Severity,

    @SerializedName("section_a")
    @Json(name = "section_a")
    val sectionA: String,

    @SerializedName("section_b")
    @Json(name = "section_b")
    val sectionB: String,

    @SerializedName("description")
    @Json(name = "description")
    val description: String,

    @SerializedName("recommended_correction")
    @Json(name = "recommended_correction")
    val recommendedCorrection: String,

    @SerializedName("explanation")
    @Json(name = "explanation")
    val explanation: ExplanationDetail? = null,

    @SerializedName("start_char_offset_a")
    @Json(name = "start_char_offset_a")
    val startCharOffsetA: Int? = null,

    @SerializedName("end_char_offset_a")
    @Json(name = "end_char_offset_a")
    val endCharOffsetA: Int? = null,

    @SerializedName("start_char_offset_b")
    @Json(name = "start_char_offset_b")
    val startCharOffsetB: Int? = null,

    @SerializedName("end_char_offset_b")
    @Json(name = "end_char_offset_b")
    val endCharOffsetB: Int? = null
)

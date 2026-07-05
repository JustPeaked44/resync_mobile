package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DuplicateItem(
    @SerializedName("section_a")
    @Json(name = "section_a")
    val sectionA: String,

    @SerializedName("section_b")
    @Json(name = "section_b")
    val sectionB: String,

    @SerializedName("similarity_score")
    @Json(name = "similarity_score")
    val similarityScore: Double,

    @SerializedName("matched_text")
    @Json(name = "matched_text")
    val matchedText: String,

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

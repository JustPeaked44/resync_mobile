package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class LinkStatus {
    @SerializedName("Validated")
    @Json(name = "Validated")
    VALIDATED,

    @SerializedName("Unresolved")
    @Json(name = "Unresolved")
    UNRESOLVED,

    @SerializedName("Broken")
    @Json(name = "Broken")
    BROKEN
}

@JsonClass(generateAdapter = true)
data class CitationItem(
    @SerializedName("citation_text")
    @Json(name = "citation_text")
    val citationText: String,

    @SerializedName("link_status")
    @Json(name = "link_status")
    val linkStatus: LinkStatus,

    @SerializedName("detailed_explanation")
    @Json(name = "detailed_explanation")
    val detailedExplanation: String,

    @SerializedName("start_char_offset")
    @Json(name = "start_char_offset")
    val startCharOffset: Int? = null,

    @SerializedName("end_char_offset")
    @Json(name = "end_char_offset")
    val endCharOffset: Int? = null
)

package com.jyodroid.jobsity.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SeriesResponse(
    val id: Int,
    val name: String,
    val image: ImageResponse,
    val rating: SeriesRating?,
    val genres: List<String>
)
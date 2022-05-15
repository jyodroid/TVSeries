package com.jyodroid.jobsity.model.dto

import com.jyodroid.jobsity.model.business.Series
import kotlinx.serialization.Serializable

@Serializable
data class SeriesResponse(
    val id: Int,
    val name: String,
    val image: ImageResponse?,
    val rating: SeriesRating?,
    val genres: List<String>
)

fun SeriesResponse.toSeries(): Series {
    return Series(
        name = this.name,
        posterUrl = this.image?.medium ?: "",
        genres = this.genres,
        averageRating = this.rating?.average
    )
}
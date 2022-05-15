package com.jyodroid.jobsity.model.dto

import androidx.core.text.HtmlCompat
import com.jyodroid.jobsity.model.business.Series
import com.jyodroid.jobsity.utils.convertToDate
import kotlinx.serialization.Serializable

@Serializable
data class SeriesResponse(
    val id: Int,
    val name: String,
    val image: ImageResponse?,
    val rating: SeriesRating?,
    val genres: List<String>,
    val summary: String?,
    val premiered: String?,
    val ended: String?,
    val schedule: SeriesScheduleResponse?,
    val network: NetworkResponse?
)

fun SeriesResponse.toSeries(): Series {
    return Series(
        name = this.name,
        posterUrl = this.image?.medium ?: "",
        genres = this.genres,
        averageRating = this.rating?.average,
        summary = HtmlCompat.fromHtml(summary ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
        premiered = premiered?.convertToDate(),
        ended = ended?.convertToDate(),
        networkName = network?.name,
        days = schedule?.days,
        time = schedule?.time
    )
}
package com.jyodroid.tvseries.model.dto

import androidx.core.text.HtmlCompat
import com.jyodroid.tvseries.model.business.Episode
import com.jyodroid.tvseries.utils.convertToDate
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeResponse(
    val name: String,
    val season: Int?,
    val number: Int?,
    val image: ImageResponse?,
    val summary: String?,
    val rating: SeriesRating?,
    val airdate: String?
)

fun EpisodeResponse.toEpisode(): Episode {
    return Episode(
        name = this.name,
        number = this.number,
        season = this.season,
        mainPosterUrl = image?.original ?: image?.medium,
        summary = HtmlCompat.fromHtml(summary ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
        rating = rating?.average,
        emissionDate = airdate?.convertToDate()
    )
}
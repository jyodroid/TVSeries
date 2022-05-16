package com.jyodroid.tvseries.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SeriesSearchResponse(val score: Float, val show: SeriesResponse)
package com.jyodroid.tvseries.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(val medium: String? = null, val original: String? = null)

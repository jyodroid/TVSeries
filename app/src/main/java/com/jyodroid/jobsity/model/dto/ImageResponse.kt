package com.jyodroid.jobsity.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(val medium: String? = null, val original: String? = null)

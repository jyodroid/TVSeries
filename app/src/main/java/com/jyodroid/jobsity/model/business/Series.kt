package com.jyodroid.jobsity.model.business

data class Series(
    val name: String,
    val posterUrl: String,
    val genres: List<String>? = null,
    val averageRating: Float? = null
)
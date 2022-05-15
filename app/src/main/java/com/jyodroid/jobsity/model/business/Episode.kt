package com.jyodroid.jobsity.model.business

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Episode(
    val name: String,
    val number: Int?,
    val season: Int?,
    val emissionDate: Date?,
    val summary: String?,
    val mainPosterUrl: String?,
    val rating: Float?
) : Parcelable

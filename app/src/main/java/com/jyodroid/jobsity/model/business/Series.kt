package com.jyodroid.jobsity.model.business

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Series(
    val name: String,
    val posterUrl: String,
    val genres: List<String>? = null,
    val averageRating: Float? = null,
    val summary: String? = null,
    val premiered: Date? = null,
    val ended: Date? = null,
    val networkName: String? = null,
    val days: List<String>? = null,
    val time: String? = null
) : Parcelable
package com.jyodroid.jobsity.utils

import java.text.SimpleDateFormat
import java.util.*

const val MAZE_DATE_FORMAT = "yyyy-MM-dd"

fun String.convertToDate(): Date? {
    val dateFormat = SimpleDateFormat(MAZE_DATE_FORMAT, Locale.getDefault())
    return dateFormat.parse(this)
}

package com.jyodroid.tvseries.utils

import java.text.SimpleDateFormat
import java.util.*

const val MAZE_DATE_FORMAT = "yyyy-MM-dd"
const val COMPLETE_DATE_FORMAT = "EEE, MMM dd, yyyy"

fun String.convertToDate(): Date? {
    val dateFormat = SimpleDateFormat(MAZE_DATE_FORMAT, Locale.getDefault())
    return dateFormat.parse(this)
}

fun Date.convertToCompleteStringDate(): String? {
    val dateFormat = SimpleDateFormat(COMPLETE_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(this)
}
package com.jyodroid.tvseries.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

const val MAZE_DATE_FORMAT = "yyyy-MM-dd"
const val COMPLETE_DATE_FORMAT = "EEEE, MMM dd, yyyy"

private const val TAG = "com.jyodroid.tvseries.utils.DateTimeFormatter"

fun String.convertToDate(): Date? {
    val dateFormat = SimpleDateFormat(MAZE_DATE_FORMAT, Locale.getDefault())
    return try {
        dateFormat.parse(this)
    } catch (e: Exception) {
        Log.e(TAG,"String To Date", e)
        null
    }
}

fun Date.convertToCompleteStringDate(): String? {
    val dateFormat = SimpleDateFormat(COMPLETE_DATE_FORMAT, Locale.getDefault())
    return try {
        dateFormat.format(this)
    } catch (e: Exception) {
        Log.e(TAG,"String To Date", e)
        null
    }
}
package com.jyodroid.tvseries.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SeriesScheduleResponse(val time: String?, val days:List<String>)

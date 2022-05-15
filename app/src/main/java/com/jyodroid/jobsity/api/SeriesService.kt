package com.jyodroid.jobsity.api

import com.jyodroid.jobsity.api.networkresponse.NetworkResponse
import com.jyodroid.jobsity.model.dto.ErrorResponse
import com.jyodroid.jobsity.model.dto.SeriesResponse
import com.jyodroid.jobsity.model.dto.SeriesSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SeriesService {
    @GET("shows")
    suspend fun getSeries(@Query("page") page: Int):
            NetworkResponse<List<SeriesResponse>, ErrorResponse>

    @GET("search/shows")
    suspend fun searchSeries(@Query("q") query: String):
            NetworkResponse<List<SeriesSearchResponse>, ErrorResponse>
}
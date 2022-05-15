package com.jyodroid.jobsity.api

import com.jyodroid.jobsity.api.networkresponse.NetworkResponse
import com.jyodroid.jobsity.model.dto.EpisodeResponse
import com.jyodroid.jobsity.model.dto.ErrorResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface EpisodeService {
    @GET("shows/{showId}/episodes")
    suspend fun getEpisodesBysShowId(@Path("showId") showId: Int):
            NetworkResponse<List<EpisodeResponse>, ErrorResponse>
}
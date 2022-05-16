package com.jyodroid.tvseries.api

import com.jyodroid.tvseries.api.networkresponse.NetworkResponse
import com.jyodroid.tvseries.model.dto.EpisodeResponse
import com.jyodroid.tvseries.model.dto.ErrorResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface EpisodeService {
    @GET("shows/{showId}/episodes")
    suspend fun getEpisodesBysShowId(@Path("showId") showId: Int):
            NetworkResponse<List<EpisodeResponse>, ErrorResponse>
}
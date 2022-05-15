package com.jyodroid.jobsity.repository

import com.jyodroid.jobsity.api.EpisodeService
import com.jyodroid.jobsity.api.networkresponse.NetworkResponse
import com.jyodroid.jobsity.model.business.Episode
import com.jyodroid.jobsity.model.dto.Result
import com.jyodroid.jobsity.model.dto.toEpisode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRepository @Inject constructor(private val episodeService: EpisodeService) {
    suspend fun getEpisodeList(seriesId: Int): Result<List<Episode>> {
        return when (val response = episodeService.getEpisodesBysShowId(seriesId)) {
            is NetworkResponse.ApiError -> Result.failure(response)
            is NetworkResponse.NetworkError -> Result.failure(response.error)
            is NetworkResponse.Success -> Result.success(response.body.map { it.toEpisode() })
            is NetworkResponse.UnknownError ->
                Result.failure(response.error ?: Error("Unknown Error"))
        }
    }

}
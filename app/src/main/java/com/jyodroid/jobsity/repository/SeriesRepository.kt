package com.jyodroid.jobsity.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jyodroid.jobsity.api.SeriesService
import com.jyodroid.jobsity.model.business.Series
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

private const val TV_MAZE_MAX_PAGE_SIZE = 250

@Singleton
class SeriesRepository @Inject constructor(private val seriesService: SeriesService) {
//    suspend fun getSeries(): Result<List<Series>> {
//        return when (val response = seriesService.getSeries(1)) {
//            is NetworkResponse.ApiError -> Result.failure(response)
//            is NetworkResponse.NetworkError -> Result.failure(response.error)
//            is NetworkResponse.Success -> Result.success(response.body.map { it.toSeries() })
//            is NetworkResponse.UnknownError -> Result.failure(Error("Unknown Error"))
//        }
//    }

    fun getShows(): Flow<PagingData<Series>> {
        return Pager(
            config = PagingConfig(
                pageSize = TV_MAZE_MAX_PAGE_SIZE,
                maxSize = 1000,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SeriesPagingSource(seriesService) }
        ).flow
    }
}
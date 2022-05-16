package com.jyodroid.tvseries.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jyodroid.tvseries.api.SeriesService
import com.jyodroid.tvseries.api.networkresponse.NetworkResponse
import com.jyodroid.tvseries.model.business.Series
import com.jyodroid.tvseries.model.dto.toSeries

private const val TV_MAZE_STARTING_PAGE = 0

class SeriesPagingSource(
    private val seriesService: SeriesService
) : PagingSource<Int, Series>() {
    override fun getRefreshKey(state: PagingState<Int, Series>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Series> {
        val position = params.key ?: TV_MAZE_STARTING_PAGE
        return when (val response = seriesService.getSeries(position)) {
            is NetworkResponse.ApiError -> LoadResult.Error(Error(response.body?.message))
            is NetworkResponse.NetworkError -> LoadResult.Error(response.error)
            is NetworkResponse.Success -> {
                val series = response.body.map { it.toSeries() }
                val nextKey = if (series.isEmpty()) {
                    null
                } else position + 1

                return LoadResult.Page(
                    data = series,
                    prevKey = if (position == TV_MAZE_STARTING_PAGE) null else position - 1,
                    nextKey = nextKey
                )
            }
            is NetworkResponse.UnknownError -> LoadResult.Error((Error("Unknown Error")))
        }
    }
}
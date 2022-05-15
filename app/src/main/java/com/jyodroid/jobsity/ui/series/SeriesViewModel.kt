package com.jyodroid.jobsity.ui.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jyodroid.jobsity.model.business.Series
import com.jyodroid.jobsity.repository.SeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SeriesViewModel @Inject constructor(private val seriesRepository: SeriesRepository) :
    ViewModel() {
    val pagingDataFlow: Flow<PagingData<Series>>

    init {
        pagingDataFlow = getSeries().cachedIn(viewModelScope)
    }

    private fun getSeries(): Flow<PagingData<Series>> = seriesRepository.getShows()
}
package com.jyodroid.tvseries.ui.series

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jyodroid.tvseries.model.business.Series
import com.jyodroid.tvseries.model.dto.Result
import com.jyodroid.tvseries.repository.SeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesViewModel @Inject constructor(private val seriesRepository: SeriesRepository) :
    ViewModel() {
    private val logTag = SeriesViewModel::class.java.canonicalName

    var query: String? = null

    val pagingDataFlow: Flow<PagingData<Series>>
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
    private val _seriesResultLiveData = MutableLiveData<List<Series>?>()
    val seriesResultLiveData: LiveData<List<Series>?>
        get() = _seriesResultLiveData

    init {
        pagingDataFlow = getSeries().cachedIn(viewModelScope)
    }

    private fun getSeries(): Flow<PagingData<Series>> = seriesRepository.getShows()

    fun searchSeries(query: String) {
        this.query = query
        viewModelScope.launch {
            when (val result = seriesRepository.searchShows(query)) {
                is Result.Failure -> {
                    Log.e(
                        logTag,
                        "API Error code:${result.e.code} with message: ${result.e.body?.message}"
                    )
                    val code = result.e.code
                    _errorLiveData.postValue(result.e.body?.message ?: "")
                }
                is Result.UnknownFailure -> {
                    Log.e(logTag, "Unknown error", result.e)
                    _errorLiveData.postValue(result.e?.message ?: "")
                }
                is Result.Success -> _seriesResultLiveData.postValue(result.data)
            }
        }
    }
}
package com.jyodroid.jobsity.ui.episode

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jyodroid.jobsity.model.business.Episode
import com.jyodroid.jobsity.model.dto.Result
import com.jyodroid.jobsity.repository.EpisodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(private val episodeRepository: EpisodeRepository) :
    ViewModel() {
    private val logTag = EpisodeViewModel::class.java.canonicalName

    private val _episodesLiveData = MutableLiveData<List<Episode>?>()
    val episodesLiveData: LiveData<List<Episode>?>
        get() = _episodesLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    fun getEpisodeList(seriesId: Int) {
        viewModelScope.launch {
            when (val result = episodeRepository.getEpisodeList(seriesId)) {
                is Result.Failure -> {
                    Log.e(
                        logTag,
                        "API Error code:${result.e.code} with message: ${result.e.body?.message}"
                    )
                    _errorLiveData.postValue(result.e.body?.message ?: "")
                }
                is Result.UnknownFailure -> {
                    Log.e(logTag, "Unknown error", result.e)
                    _errorLiveData.postValue(result.e?.message ?: "")
                }
                is Result.Success -> _episodesLiveData.postValue(result.data)
            }
        }
    }
}
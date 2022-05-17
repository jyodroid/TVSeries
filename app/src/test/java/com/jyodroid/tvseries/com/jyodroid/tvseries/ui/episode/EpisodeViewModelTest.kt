package com.jyodroid.tvseries.com.jyodroid.tvseries.ui.episode

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.jyodroid.tvseries.com.jyodroid.tvseries.testutils.TestCoroutineRule
import com.jyodroid.tvseries.model.business.Episode
import com.jyodroid.tvseries.model.dto.Result
import com.jyodroid.tvseries.repository.EpisodeRepository
import com.jyodroid.tvseries.ui.episode.EpisodeViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EpisodeViewModelTest {
    @get:Rule
    val testInstantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    lateinit var episodeRepository: EpisodeRepository

    @MockK(relaxed = true)
    lateinit var episodesObserver: Observer<List<Episode>?>

    @MockK(relaxed = true)
    lateinit var episodesErrorObserver: Observer<String>

    @InjectMockKs
    lateinit var episodeViewModel: EpisodeViewModel

    @Before
    fun prepare() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        episodeViewModel.episodesLiveData.removeObserver(episodesObserver)
        episodeViewModel.errorLiveData.removeObserver(episodesErrorObserver)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `When search series by name and get results`() = runTest {
        episodeViewModel.episodesLiveData.observeForever(episodesObserver)
        episodeViewModel.errorLiveData.observeForever(episodesErrorObserver)

        val seriesId = 1
        val episode = mockk<Episode>()
        val episodeList = listOf(episode)
        val resultEpisode = Result.success(episodeList)

        coEvery {
            episodeRepository.getEpisodeList(seriesId)
        } returns resultEpisode

        episodeViewModel.getEpisodeList(seriesId)

        coVerify(exactly = 1) {
            episodeRepository.getEpisodeList(seriesId)
            episodesObserver.onChanged(episodeList)
        }

        coVerify(exactly = 0, timeout = 1000) {
            episodesErrorObserver.onChanged(any())
        }

        assertEquals(episodeList, episodeViewModel.episodesLiveData.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `When search series by name and get error`() = runTest {
        episodeViewModel.episodesLiveData.observeForever(episodesObserver)
        episodeViewModel.errorLiveData.observeForever(episodesErrorObserver)

        val seriesId = 1
        val errorMessage = "No Internet Connection"
        val resultEpisode = Result.failure<List<Episode>>(Error(errorMessage))

        coEvery {
            episodeRepository.getEpisodeList(seriesId)
        } returns resultEpisode

        episodeViewModel.getEpisodeList(seriesId)

        coVerify(exactly = 1) {
            episodeRepository.getEpisodeList(seriesId)
            episodesErrorObserver.onChanged(errorMessage)
        }

        coVerify(exactly = 0, timeout = 1000) {
            episodesObserver.onChanged(any())
        }

        assertEquals(errorMessage, episodeViewModel.errorLiveData.value)
    }
}
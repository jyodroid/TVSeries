package com.jyodroid.tvseries.com.jyodroid.tvseries.ui.series

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.jyodroid.tvseries.com.jyodroid.tvseries.testutils.TestCoroutineRule
import com.jyodroid.tvseries.model.business.Series
import com.jyodroid.tvseries.model.dto.Result
import com.jyodroid.tvseries.repository.SeriesRepository
import com.jyodroid.tvseries.ui.series.SeriesViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SeriesViewModelTest {
    @get:Rule
    val testInstantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK(relaxed = true)
    lateinit var seriesRepository: SeriesRepository

    @MockK(relaxed = true)
    lateinit var seriesSearchObserver: Observer<List<Series>?>

    @MockK(relaxed = true)
    lateinit var seriesSearchErrorObserver: Observer<String>

    @InjectMockKs
    lateinit var seriesViewModel: SeriesViewModel

    @Before
    fun prepare() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        seriesViewModel.seriesResultLiveData.removeObserver(seriesSearchObserver)
        seriesViewModel.errorLiveData.removeObserver(seriesSearchErrorObserver)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `When search series by name and get results`() = runTest {
        seriesViewModel.seriesResultLiveData.observeForever(seriesSearchObserver)
        seriesViewModel.errorLiveData.observeForever(seriesSearchErrorObserver)

        val query = "query"
        val seriesResult = Series(1, "test", "testUrl")
        val seriesListResult = listOf(seriesResult)
        val result = Result.success(seriesListResult)

        coEvery {
            seriesRepository.searchShows(query)
        } returns result

        seriesViewModel.searchSeries(query)

        coVerify(exactly = 1) {
            seriesRepository.searchShows(query)
            seriesSearchObserver.onChanged(seriesListResult)
        }

        coVerify(exactly = 0, timeout = 1000) {
            seriesSearchErrorObserver.onChanged(any())
        }

        assertEquals(seriesListResult, seriesViewModel.seriesResultLiveData.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `When search series by name and get exception`() = runTest {
        seriesViewModel.seriesResultLiveData.observeForever(seriesSearchObserver)
        seriesViewModel.errorLiveData.observeForever(seriesSearchErrorObserver)

        val query = "query"
        val errorMessage = "No Internet Connection"
        val result = Result.failure<List<Series>>(Error(errorMessage))

        coEvery {
            seriesRepository.searchShows(query)
        } returns result

        seriesViewModel.searchSeries(query)

        coVerify(exactly = 1) {
            seriesRepository.searchShows(query)
            seriesSearchErrorObserver.onChanged(errorMessage)
        }

        coVerify(exactly = 0, timeout = 1000) {
            seriesSearchObserver.onChanged(any())
        }

        assertEquals(errorMessage, seriesViewModel.errorLiveData.value)
    }
}
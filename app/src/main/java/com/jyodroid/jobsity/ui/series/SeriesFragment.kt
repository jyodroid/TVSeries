package com.jyodroid.jobsity.ui.series

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jyodroid.jobsity.databinding.FragmentSeriesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeriesFragment : Fragment() {

    private var _binding: FragmentSeriesBinding? = null
    private val binding get() = _binding!!

    private val seriesViewModel by viewModels<SeriesViewModel>()
    private val seriesAdapter by lazy { SeriesAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesBinding.inflate(inflater, container, false)

        binding.seriesList.configure()
        collectUIState()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectUIState() {
        viewLifecycleOwner.lifecycleScope.launch {
            seriesViewModel.pagingDataFlow.collectLatest { seriesPaging ->
                seriesAdapter.submitData(seriesPaging)
            }
        }
    }

    private fun RecyclerView.configure() {
        adapter = seriesAdapter
        layoutManager = LinearLayoutManager(activity)
    }
}
package com.jyodroid.jobsity.ui.series

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jyodroid.jobsity.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesBinding.inflate(inflater, container, false)

        binding.seriesList.configure()
        initObservers()
        collectUIState()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as? SearchView
        searchView.configure()
        super.onCreateOptionsMenu(menu, inflater)
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

    private fun SearchView?.configure() {
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        this?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            queryHint = getString(R.string.search_hint)
            maxWidth = Integer.MAX_VALUE

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    seriesViewModel.searchSeries(query)
                    return true
                }

                override fun onQueryTextChange(s: String): Boolean {
                    return true
                }
            })

            setOnCloseListener { closeSearch() }
            setOnSearchClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    seriesAdapter.submitData(PagingData.empty())
                }
            }
        }
    }

    private fun closeSearch(): Boolean {
        collectUIState()
        return false
    }

    private fun initObservers() {
        seriesViewModel.seriesResultLiveData.observe(viewLifecycleOwner) { series ->
            if (series.isNullOrEmpty()) {
                //TODO: Show no result placeholder
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    val pagingData = PagingData.from(data = series.toMutableList())
                    seriesAdapter.submitData(pagingData)
                }
            }
        }

        seriesViewModel.errorLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
            //TODO handle error in a better way
        }
    }
}

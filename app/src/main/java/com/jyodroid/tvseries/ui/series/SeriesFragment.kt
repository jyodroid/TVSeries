package com.jyodroid.tvseries.ui.series

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jyodroid.tvseries.R
import com.jyodroid.tvseries.databinding.FragmentSeriesBinding
import com.jyodroid.tvseries.model.business.Series
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeriesFragment : SeriesAdapter.SeriesListener, Fragment() {

    private var _binding: FragmentSeriesBinding? = null
    private val binding get() = _binding!!

    private val seriesViewModel by viewModels<SeriesViewModel>()
    private val seriesAdapter by lazy { SeriesAdapter(this) }

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
        val columnCount = resources.getInteger(R.integer.series_grid_size)
        val gridLayoutManager =
            GridLayoutManager(activity, columnCount, GridLayoutManager.VERTICAL, false)
        layoutManager = gridLayoutManager
    }

    private fun SearchView?.configure() {
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        this?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            queryHint = getString(R.string.search_hint)
            maxWidth = Integer.MAX_VALUE
            isIconified = true

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    seriesViewModel.searchSeries(query)
                    return true
                }

                override fun onQueryTextChange(s: String): Boolean {
                    return true
                }
            })

            setOnCloseListener {
                if (query.isNullOrEmpty()) {
                    onActionViewCollapsed()
                    closeSearch()
                } else true

            }
            setOnSearchClickListener {
                initObservers()
                viewLifecycleOwner.lifecycleScope.launch {
                    seriesAdapter.submitData(PagingData.empty())
                }
            }

            if (seriesViewModel.query != null) {
                initObservers()
                this.requestFocus()
                this.setQuery(seriesViewModel.query, true)
                isIconified = false
            }
        }
    }

    private fun closeSearch(): Boolean {
        seriesViewModel.query = null
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

    override fun onSeriesSelected(series: Series) {
        val directions = SeriesFragmentDirections.navigateToSeriesDetails(series)
        findNavController().navigate(directions)
    }
}

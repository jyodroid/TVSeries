package com.jyodroid.tvseries.ui.series

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jyodroid.tvseries.R
import com.jyodroid.tvseries.databinding.FragmentSeriesBinding
import com.jyodroid.tvseries.model.business.Series
import com.jyodroid.tvseries.ui.lockscreen.LockViewModel
import com.jyodroid.tvseries.ui.settings.enablePinKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.UnknownHostException

@AndroidEntryPoint
class SeriesFragment : SeriesAdapter.SeriesListener, Fragment() {

    private var _binding: FragmentSeriesBinding? = null
    private val binding get() = _binding!!

    private val lockViewModel by activityViewModels<LockViewModel>()
    private val seriesViewModel by activityViewModels<SeriesViewModel>()
    private val seriesAdapter by lazy {
        SeriesAdapter(this)
    }

    private val alertDialog by lazy {
        AlertDialog.Builder(requireContext()).also {
            it.setPositiveButton(android.R.string.ok, null)
        }.create()
    }

    private val prefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

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

        with(binding) {
            seriesSwipeRefresh.configure()
            seriesList.configure()
        }

        collectUIState()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLockScreenObserver()
        val isEnabledPinLock = prefs.getBoolean(enablePinKey, false)
        if (isEnabledPinLock) {
            if (lockViewModel.screenLockedLiveData.value == null) {
                val navController = findNavController()
                navController.navigate(R.id.navigation_lock_screen)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as? SearchView
        searchView.configure()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.lock_screen -> {
                val isEnabledPinLock = prefs.getBoolean(enablePinKey, false)
                if (isEnabledPinLock) {
                    lockViewModel.lockScreen()
                } else {
                    showAlertDialog(R.string.error_alert, getString(R.string.pin_lock_error))
                }
                true
            }
            R.id.settings -> {
                val directions = SeriesFragmentDirections.navigateToSettings()
                findNavController().navigate(directions)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSeriesSelected(series: Series) {
        val directions = SeriesFragmentDirections.navigateToSeriesDetails(series)
        findNavController().navigate(directions)
    }

    private fun collectUIState() {
        viewLifecycleOwner.lifecycleScope.launch {
            seriesViewModel.pagingDataFlow.collectLatest { seriesPaging ->
                seriesAdapter.submitData(seriesPaging)
            }
        }
    }

    private fun RecyclerView.configure() {
        seriesAdapter.addLoadStateListener { combinedLoadStates ->
            if (combinedLoadStates.refresh is LoadState.Error) {
                val errorState = (combinedLoadStates.refresh as? LoadState.Error)?.error
                val message =
                    if (errorState is UnknownHostException) {
                        requireContext().getString(R.string.please_check_internet)
                    } else {
                        requireContext().getString(
                            R.string.error_alert_description,
                            errorState?.message ?: context.getString(R.string.unknown_error_message)
                        )
                    }
                showAlertDialog(
                    R.string.error_alert,
                    getString(
                        R.string.error_alert_description,
                        message
                    )
                )
                binding.seriesSwipeRefresh.isRefreshing = false
            }
            if(combinedLoadStates.refresh is LoadState.NotLoading){
                kotlin.runCatching { binding.seriesSwipeRefresh.isRefreshing = false }
            }
        }

        adapter = seriesAdapter.withLoadStateHeaderAndFooter(
            header = SeriesLoadingAdapter(seriesAdapter::retry),
            footer = SeriesLoadingAdapter(seriesAdapter::retry)
        )
        val columnCount = resources.getInteger(R.integer.series_grid_size)
        val gridLayoutManager =
            GridLayoutManager(activity, columnCount, GridLayoutManager.VERTICAL, false)
        layoutManager = gridLayoutManager
        val space = resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        addItemDecoration(SeriesGridItemDecorator(space))
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
                    binding.seriesSwipeRefresh.isEnabled = false
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
        binding.seriesSwipeRefresh.isEnabled = true
        return false
    }

    private fun initObservers() {
        seriesViewModel.seriesResultLiveData.observe(viewLifecycleOwner) { series ->
            if (series.isNullOrEmpty()) {
                showAlertDialog(
                    R.string.search_alert,
                    getString(R.string.not_results_found, seriesViewModel.query)
                )
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    val pagingData = PagingData.from(data = series.toMutableList())
                    seriesAdapter.submitData(pagingData)
                }
            }
        }

        seriesViewModel.errorLiveData.observe(viewLifecycleOwner) {
            showAlertDialog(R.string.error_alert, getString(R.string.error_alert_description, it))
        }
    }

    private fun initLockScreenObserver() {
        lockViewModel.screenLockedLiveData.observe(viewLifecycleOwner) { locked ->
            if (locked) {
                val navController = findNavController()
                navController.navigate(R.id.navigation_lock_screen)
            }
        }
    }

    private fun showAlertDialog(@StringRes title: Int, message: String) {
        alertDialog.apply {
            setTitle(title)
            setMessage(message)
            show()
        }
    }

    private fun SwipeRefreshLayout.configure() {
        this.setOnRefreshListener { seriesAdapter.refresh() }
    }
}

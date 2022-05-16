package com.jyodroid.tvseries.ui.series

import android.content.res.Configuration
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jyodroid.tvseries.R
import com.jyodroid.tvseries.databinding.FragmentSeriesDetailBinding
import com.jyodroid.tvseries.model.business.Episode
import com.jyodroid.tvseries.ui.episode.EpisodeAdapter
import com.jyodroid.tvseries.ui.episode.EpisodeItem
import com.jyodroid.tvseries.ui.episode.EpisodeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeriesDetailFragment : EpisodeAdapter.EpisodeListener, Fragment() {
    private var _binding: FragmentSeriesDetailBinding? = null
    private val binding get() = _binding!!

    private val episodeViewModel by viewModels<EpisodeViewModel>()

    private val args: SeriesDetailFragmentArgs by navArgs()

    private val episodeAdapter by lazy {
        EpisodeAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesDetailBinding.inflate(inflater, container, false)
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) configureToolbar()
        else setHasOptionsMenu(true)
        initUI()
        initObservers()
        if (episodeViewModel.episodesLiveData.value == null) {
            episodeViewModel.getEpisodeList(args.series.id)
        }
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> findNavController().popBackStack()
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onEpisodeSelected(episode: Episode) {
        val directions = SeriesDetailFragmentDirections.navigateToEpisodeDetails(episode)
        findNavController().navigate(directions)
    }

    private fun initUI() {
        val series = args.series

        Glide
            .with(this)
            .load(series.mainPosterUrl ?: series.posterUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_image_regular)
            .error(R.drawable.ic_image_regular)
            .into(binding.seriesDetailPoster)

        binding.details.apply {
            seriesDetailName.text = series.name
            seriesDetailsSummary.text = series.summary
            seriesDetailGenres.text = series.genres?.joinToString(" - ")
            val days = series.days?.joinToString(", ")
            seriesDetailsAiredInfo.text = getString(
                R.string.series_detail_aired_info,
                days,
                series.time,
                series.networkName ?: ""
            )

            if (series.premiered != null) {
                val calendar = Calendar.getInstance()
                calendar.time = series.premiered
                val startYear = calendar.get(Calendar.YEAR).toString()
                val endYear = if (series.ended == null) {
                    getString(R.string.now)
                } else {
                    calendar.time = series.ended
                    calendar.get(Calendar.YEAR)
                }
                seriesDetailsAired.text =
                    getString(R.string.series_detail_aired, startYear, endYear)
            }

            seriesDetailsEpisodesList.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = episodeAdapter
                val itemDecor = DividerItemDecoration(context, RecyclerView.VERTICAL)
                addItemDecoration(itemDecor)
            }
        }
    }

    private fun initObservers() {
        episodeViewModel.episodesLiveData.observe(viewLifecycleOwner) { episodes ->
            val seasons = episodes?.groupBy { it.season }

            val episodeItemList = ArrayList<EpisodeItem>()
            seasons?.forEach { entry ->
                val season = EpisodeItem.Season(entry.key ?: -1)
                episodeItemList.add(season)

                val seasonEpisodes =
                    entry.value.sortedBy { it.number }.map { EpisodeItem.EpisodeData(it) }
                episodeItemList.addAll(seasonEpisodes)
            }

            episodeAdapter.submitList(episodeItemList)
        }

        episodeViewModel.errorLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
            //TODO handle error in a better way
        }
    }

    private fun configureToolbar() {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        binding.seriesDetailsToolbar?.apply {
            setNavigationIcon(androidx.constraintlayout.widget.R.drawable.abc_ic_ab_back_material)
            val navColor = ContextCompat.getColor(requireContext(), R.color.white)
            navigationIcon?.setTint(navColor)
            setNavigationOnClickListener { findNavController().popBackStack() }
            title = getString(R.string.series_detail_title)
        }
        binding.collapsingToolbarLayout?.setExpandedTitleColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
    }
}
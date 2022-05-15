package com.jyodroid.jobsity.ui.series

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jyodroid.jobsity.R
import com.jyodroid.jobsity.databinding.FragmentSeriesDetailBinding

class SeriesDetailFragment : Fragment() {
    private var _binding: FragmentSeriesDetailBinding? = null
    private val binding get() = _binding!!

    private val args: SeriesDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUI()
        super.onViewCreated(view, savedInstanceState)
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

    private fun initUI() {
        val series = args.series

        Glide
            .with(this)
            .load(series.posterUrl)
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
        }
    }
}
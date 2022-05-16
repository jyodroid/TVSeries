package com.jyodroid.tvseries.ui.episode

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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.jyodroid.tvseries.R
import com.jyodroid.tvseries.databinding.FragmentEpisodeBinding
import com.jyodroid.tvseries.utils.convertToCompleteStringDate

class EpisodeFragment : Fragment() {
    private var _binding: FragmentEpisodeBinding? = null
    private val binding get() = _binding!!

    private val args: EpisodeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        initUI()
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

    private fun initUI() {
        val episode = args.episode

        binding.episodeName.text = episode.name
        binding.episodeLabel.text =
            getString(R.string.episode_label, episode.season, episode.number)
        binding.episodePremierDate.text =
            getString(
                R.string.episode_premiered_label,
                episode.emissionDate?.convertToCompleteStringDate()
            )

        binding.episodeSummary.text = episode.summary

        Glide
            .with(this)
            .load(episode.mainPosterUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_tv_solid)
            .error(R.drawable.ic_tv_solid)
            .transition(withCrossFade())
            .into(binding.episodePoster)
    }

}
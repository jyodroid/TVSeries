package com.jyodroid.jobsity.ui.episode

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jyodroid.jobsity.R
import com.jyodroid.jobsity.databinding.EpisodeListItemBinding
import com.jyodroid.jobsity.databinding.EpisodeListSeasonItemBinding
import com.jyodroid.jobsity.model.business.Episode

class EpisodeAdapter(private val episodeListener: EpisodeListener) :
    ListAdapter<EpisodeItem, RecyclerView.ViewHolder>(EpisodeDiffCallBack()) {

    private val itemViewTypeSeason = 0
    private val itemViewTypeItem = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemViewTypeSeason -> {
                val seasonView = EpisodeListSeasonItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SeasonViewHolder(seasonView)
            }
            itemViewTypeItem -> {
                val episodeView = EpisodeListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ViewHolder(episodeView)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val episodeItem = getItem(position)
        when (holder) {
            is ViewHolder -> holder.bind((episodeItem as EpisodeItem.EpisodeData).episode)
            is SeasonViewHolder -> holder.bind((episodeItem as EpisodeItem.Season).number)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is EpisodeItem.Season -> itemViewTypeSeason
            is EpisodeItem.EpisodeData -> itemViewTypeItem
        }
    }

    inner class ViewHolder(private val view: EpisodeListItemBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(episode: Episode) {
            view.episodeNumber.text = episode.number.toString()
            view.episodeName.text = episode.name
            view.episodeRating.text = episode.rating?.toString() ?: "0.0"
            view.root.setOnClickListener { episodeListener.onEpisodeSelected(episode) }
        }
    }

    inner class SeasonViewHolder(private val seasonView: EpisodeListSeasonItemBinding) :
        RecyclerView.ViewHolder(seasonView.root) {
        fun bind(season: Int) {
            val seasonText =
                if (season == -1) seasonView.root.context.getString(R.string.undefined_season)
                else seasonView.root.context.getString(R.string.season_number, season)
            seasonView.seasonNumber.text = seasonText

        }
    }

    private class EpisodeDiffCallBack : DiffUtil.ItemCallback<EpisodeItem>() {
        override fun areItemsTheSame(oldItem: EpisodeItem, newItem: EpisodeItem): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: EpisodeItem, newItem: EpisodeItem): Boolean {
            return if (oldItem is EpisodeItem.Season && newItem is EpisodeItem.Season) {
                oldItem.number == newItem.number
            } else if (oldItem is EpisodeItem.EpisodeData && newItem is EpisodeItem.EpisodeData) {
                oldItem.episode == newItem.episode
            } else false
        }
    }

    interface EpisodeListener {
        fun onEpisodeSelected(episode: Episode)
    }
}

sealed class EpisodeItem {
    class Season(val number: Int) : EpisodeItem()
    class EpisodeData(val episode: Episode) : EpisodeItem()
}
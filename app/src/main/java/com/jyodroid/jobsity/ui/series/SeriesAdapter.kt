package com.jyodroid.jobsity.ui.series

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jyodroid.jobsity.R
import com.jyodroid.jobsity.databinding.SeriesListItemBinding
import com.jyodroid.jobsity.model.business.Series

class SeriesAdapter(private val seriesListener: SeriesListener) :
    PagingDataAdapter<Series, SeriesAdapter.ViewHolder>(SeriesDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            SeriesListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val series = getItem(position)
        series?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val view: SeriesListItemBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(series: Series) {

            Glide
                .with(view.root)
                .load(series.posterUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_image_regular)
                .error(R.drawable.ic_image_regular)
                .into(view.seriesListItemPosterImage)

            view.seriesListItemName.text = series.name

            val genres = series.genres?.joinToString(", ")
            view.seriesListItemGenres.text = genres

            view.seriesListItemRating.text = series.averageRating?.toString() ?: "0.0"
            view.root.setOnClickListener { seriesListener?.onSeriesSelected(series) }
        }
    }

    private class SeriesDiffCallBack : DiffUtil.ItemCallback<Series>() {
        override fun areItemsTheSame(oldItem: Series, newItem: Series): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Series, newItem: Series): Boolean =
            oldItem == newItem
    }

    interface SeriesListener {
        fun onSeriesSelected(series: Series)
    }
}
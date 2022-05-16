package com.jyodroid.tvseries.ui.series

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jyodroid.tvseries.databinding.SeriesListNetworkStatesBinding
import com.jyodroid.tvseries.utils.hide
import com.jyodroid.tvseries.utils.show

class SeriesLoadingAdapter(private val retryCallback: () -> Unit) :
    LoadStateAdapter<SeriesLoadingAdapter.NetworkStateViewHolder>() {

    override fun onBindViewHolder(holder: NetworkStateViewHolder, loadState: LoadState) =
        holder.bind(loadState)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ) = NetworkStateViewHolder(
        SeriesListNetworkStatesBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
    )

    inner class NetworkStateViewHolder(
        private val binding: SeriesListNetworkStatesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retryButton.setOnClickListener {
                retryCallback()
            }
        }

        fun bind(loadState: LoadState) {
            with(binding) {
                when (loadState) {
                    is LoadState.Loading -> {
                        progressBar.show()
                        retryButton.hide()
                        errorMsg.hide()
                    }
                    is LoadState.Error -> {
                        val errorMessage = loadState.error.message
                        if (!errorMessage.isNullOrBlank()) {
                            errorMsg.apply {
                                text = errorMessage
                                show()
                            }
                        }
                        retryButton.show()
                        progressBar.hide()
                    }
                    else -> {
                        progressBar.hide()
                        retryButton.hide()
                        errorMsg.hide()
                    }
                }
            }
        }
    }
}
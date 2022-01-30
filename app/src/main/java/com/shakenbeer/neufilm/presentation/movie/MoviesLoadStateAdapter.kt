package com.shakenbeer.neufilm.presentation.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shakenbeer.neufilm.databinding.ItemLoadingBinding

class MoviesLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadingItemViewHolder>() {
    override fun onBindViewHolder(holder: LoadingItemViewHolder, loadState: LoadState) {
        holder.bindTo(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingItemViewHolder {
        return LoadingItemViewHolder(
            ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) { retry() }
    }
}

class LoadingItemViewHolder(
    private val binding: ItemLoadingBinding,
    private val retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry() }
    }

    fun bindTo(loadState: LoadState) {
        with(binding) {
            progressBar.isVisible = loadState is Loading
            retryButton.isVisible = loadState is Error
            errorTextView.isVisible = !(loadState as? Error)?.error?.message.isNullOrBlank()
            errorTextView.text = (loadState as? Error)?.error?.message
        }
    }
}

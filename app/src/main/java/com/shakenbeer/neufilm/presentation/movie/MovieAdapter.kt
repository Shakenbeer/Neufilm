package com.shakenbeer.neufilm.presentation.movie

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.shakenbeer.neufilm.databinding.ItemMovieBinding
import com.shakenbeer.neufilm.domain.entity.Movie

class MovieAdapter(private val onClick: (Movie) -> Unit) :
    PagingDataAdapter<Movie, MovieViewHolder>(MovieDiffCallback()) {
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        getItem(position)?.let { bind(holder, it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MovieViewHolder(
        ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) { getItem(it)?.let { movie -> onClick(movie) } }
}

private fun bind(holder: MovieViewHolder, movie: Movie) {
    with(holder.binding) {
        posterImageView.load(movie.listPosterUrl)
        titleTextView.text = movie.title
        if (movie.genre1 != null) {
            genre1TextView.visibility = VISIBLE
            genre1TextView.text = movie.genre1
        } else {
            genre1TextView.visibility = INVISIBLE
        }
        if (movie.genre2 != null) {
            genreDivider.visibility = VISIBLE
            genre2TextView.visibility = VISIBLE
            genre2TextView.text = movie.genre2
        } else {
            genreDivider.visibility = INVISIBLE
            genre2TextView.visibility = INVISIBLE
        }
        averageVoteTextView.text = movie.rating
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
        oldItem.listPosterUrl == newItem.listPosterUrl &&
                oldItem.title == newItem.title &&
                oldItem.averageVote == newItem.averageVote &&
                //display two genres only
                oldItem.genres.getOrNull(0) == newItem.genres.getOrNull(0) &&
                oldItem.genres.getOrNull(1) == newItem.genres.getOrNull(1)
}

class MovieViewHolder(
    val binding: ItemMovieBinding,
    private val clickListener: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener { clickListener(absoluteAdapterPosition) }
    }
}
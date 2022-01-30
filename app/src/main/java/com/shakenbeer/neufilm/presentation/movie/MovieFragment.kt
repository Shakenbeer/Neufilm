package com.shakenbeer.neufilm.presentation.movie

import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import com.shakenbeer.neufilm.R
import com.shakenbeer.neufilm.databinding.FragmentMovieBinding
import com.shakenbeer.neufilm.domain.GetSelectedMovieUseCase.Outcome
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MovieFragment : Fragment() {

    private val viewModel: MovieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMovieBinding.inflate(inflater, container, false)

        with(binding) {
            val actorAdapter = ActorAdapter {}

            actorsRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                val rightOffset = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    24f,
                    requireContext().resources.displayMetrics
                ).toInt()

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.right = rightOffset
                }
            })
            actorsRecyclerView.adapter = actorAdapter

            val loader = ImageLoader(requireContext())
            viewModel.selectedMovie.observe(viewLifecycleOwner) {
                when (it) {
                    is Outcome.Ok -> processOk(it, loader)
                    Outcome.Error -> processError()
                }
            }
            viewModel.actors.observe(viewLifecycleOwner) { actors ->
                castTextView.isVisible = actors.isNotEmpty()
                actorAdapter.submitList(actors)
            }
        }

        return binding.root
    }

    private fun FragmentMovieBinding.processError() {
        posterImageView.load(R.drawable.no_movie)
        titleTextView.text = getString(R.string.unexpected_outcome)
        genresTextView.text = getString(R.string.disaster)
        averageVoteTextView.text = getString(R.string.page_not_found)
    }

    private fun FragmentMovieBinding.processOk(
        ok: Outcome.Ok,
        loader: ImageLoader
    ) {
        ok.movie.run {
            val request = ImageRequest.Builder(requireContext())
                .data(listPosterUrl) // demo link
                .allowHardware(false)
                .target { result ->
                    posterImageView.setImageDrawable(result)
                    val bitmap = (result as BitmapDrawable).bitmap
                    Palette.from(bitmap).generate { palette ->
                        palette?.vibrantSwatch?.let { swatch ->
                            root.setBackgroundColor(swatch.rgb)
                        }
                    }
                }
                .build()
            loader.enqueue(request)
            titleTextView.text = title
            genresTextView.text = if (genre1 != null) {
                if (genre2 != null) {
                    "$genre1, $genre2"
                } else {
                    genre1
                }
            } else ""
            averageVoteTextView.text = rating
            plotTextView.text = overview
        }
    }
}
package com.shakenbeer.neufilm.presentation.movie

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.shakenbeer.neufilm.databinding.FragmentLatestMoviesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LatestMoviesFragment : Fragment() {

    private val viewModel: LatestMoviesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLatestMoviesBinding.inflate(inflater, container, false)

        with(binding) {

            val movieAdapter = MovieAdapter(viewModel::onMovieClicked)
            moviesRecyclerView.adapter =
                movieAdapter.withLoadStateFooter(MoviesLoadStateAdapter(movieAdapter::retry))

            loading.retryButton.setOnClickListener { movieAdapter.retry() }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    movieAdapter.loadStateFlow.collectLatest { loadStates ->
                        val init = movieAdapter.itemCount == 0
                        loading.progressBar.isVisible =
                            init && loadStates.refresh is LoadState.Loading
                        loading.errorTextView.isVisible =
                            init && !(loadStates.refresh as? LoadState.Error)?.error?.message.isNullOrBlank()
                        loading.errorTextView.text =
                            (loadStates.refresh as? LoadState.Error)?.error?.message
                        loading.retryButton.isVisible =
                            init && (loadStates.refresh is LoadState.Error)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.moviesFlow.collectLatest { pagingData ->
                        movieAdapter.submitData(pagingData)
                    }
                }
            }

            viewModel.movieSelected.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let {
                    val action =
                        LatestMoviesFragmentDirections.actionLatestMoviesFragmentToMovieFragment()
                    findNavController().navigate(action)
                }
            }
        }

        return binding.root
    }
}
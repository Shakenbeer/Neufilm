package com.shakenbeer.neufilm.presentation.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shakenbeer.neufilm.domain.GetMoviesUseCase
import com.shakenbeer.neufilm.domain.SelectMovieUseCase
import com.shakenbeer.neufilm.domain.entity.Movie
import com.shakenbeer.neufilm.presentation.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LatestMoviesViewModel @Inject constructor(
    getMovies: GetMoviesUseCase,
    private val selectMovie: SelectMovieUseCase
) : ViewModel() {

    val moviesFlow = getMovies().cachedIn(viewModelScope)

    private val _movieSelected = MutableLiveData<Event<Unit>>()
    val movieSelected: LiveData<Event<Unit>> by lazy { _movieSelected }

    fun onMovieClicked(movie: Movie) {
        selectMovie(movie)
        _movieSelected.value = Event(Unit)
    }
}
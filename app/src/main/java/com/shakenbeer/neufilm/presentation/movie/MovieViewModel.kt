package com.shakenbeer.neufilm.presentation.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakenbeer.neufilm.domain.GetActorsUseCase
import com.shakenbeer.neufilm.domain.GetSelectedMovieUseCase
import com.shakenbeer.neufilm.domain.GetSelectedMovieUseCase.*
import com.shakenbeer.neufilm.domain.entity.Actor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    getSelectedMovie: GetSelectedMovieUseCase,
    getActors: GetActorsUseCase
) : ViewModel() {

    val selectedMovie: LiveData<Outcome> by lazy { MutableLiveData(getSelectedMovie()) }

    val actors: LiveData<List<Actor>> by lazy {
        MutableLiveData<List<Actor>>().also {
            viewModelScope.launch {
                it.value = getActors()
            }
        }
    }
}
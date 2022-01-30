package com.shakenbeer.neufilm.domain

import com.shakenbeer.neufilm.domain.entity.Movie
import com.shakenbeer.neufilm.domain.repo.MovieRepo
import javax.inject.Inject

class SelectMovieUseCase @Inject constructor(private val movieRepo: MovieRepo) {

    operator fun invoke(movie: Movie) = movieRepo.setSelectedMovie(movie)
}
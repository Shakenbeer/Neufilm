package com.shakenbeer.neufilm.domain

import com.shakenbeer.neufilm.domain.entity.Movie
import com.shakenbeer.neufilm.domain.repo.MovieRepo
import javax.inject.Inject

class GetSelectedMovieUseCase@Inject constructor(private val movieRepo: MovieRepo) {

    operator fun invoke(): Outcome = movieRepo.getSelectedMovie().run {
        when  {
            isSuccess -> getOrNull()?.let { Outcome.Ok(it) } ?: Outcome.Error
            else -> Outcome.Error
        }
    }

    sealed class Outcome {
        object Error: Outcome()
        class Ok(val movie: Movie): Outcome()
    }
}
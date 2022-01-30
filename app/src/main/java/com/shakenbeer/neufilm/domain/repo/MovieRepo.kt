package com.shakenbeer.neufilm.domain.repo

import com.shakenbeer.neufilm.domain.entity.Movie

interface MovieRepo {

    suspend fun getLatestMovies(page: Int): Result<List<Movie>>

    fun setSelectedMovie(movie: Movie)

    fun getSelectedMovie(): Result<Movie>
}
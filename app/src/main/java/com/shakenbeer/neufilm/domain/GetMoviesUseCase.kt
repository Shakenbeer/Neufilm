package com.shakenbeer.neufilm.domain

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shakenbeer.neufilm.domain.entity.Movie
import com.shakenbeer.neufilm.domain.paging.MoviePagingSource
import com.shakenbeer.neufilm.domain.repo.MovieRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(private val movieRepo: MovieRepo) {
    operator fun invoke(): Flow<PagingData<Movie>> = Pager(PagingConfig(PAGE_SIZE)) {
        MoviePagingSource(movieRepo)
    }.flow

    companion object {
        const val PAGE_SIZE = 16
    }
}
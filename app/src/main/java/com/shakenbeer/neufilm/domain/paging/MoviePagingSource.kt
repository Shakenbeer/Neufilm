package com.shakenbeer.neufilm.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shakenbeer.neufilm.domain.entity.Movie
import com.shakenbeer.neufilm.domain.repo.MovieRepo

class MoviePagingSource(
    private val movieRepo: MovieRepo
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val result = movieRepo.getLatestMovies(page)
            when {
                result.isFailure -> LoadResult.Error(result.exceptionOrNull() ?: Throwable())
                result.isSuccess -> LoadResult.Page(
                    data = result.getOrThrow(),
                    prevKey = null,
                    nextKey = page + 1
                )
                else -> throw IllegalStateException()
            }

        } catch (throwable: Throwable) {
            LoadResult.Error(throwable)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>) =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
}
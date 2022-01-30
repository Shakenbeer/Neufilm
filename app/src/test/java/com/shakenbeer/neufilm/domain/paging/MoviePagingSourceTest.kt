package com.shakenbeer.neufilm.domain.paging

import androidx.paging.PagingSource
import com.shakenbeer.neufilm.domain.entity.Movie
import com.shakenbeer.neufilm.domain.repo.MovieRepo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import androidx.paging.PagingSource.LoadParams.Refresh
import androidx.paging.PagingSource.LoadResult.Page
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class MoviePagingSourceTest {

    @Mock
    private lateinit var movieRepo: MovieRepo

    private lateinit var moviePagingSource: MoviePagingSource

    @Test
    fun `check paging source`() = runTest {
        whenever(movieRepo.getLatestMovies(1)).thenReturn(
            Result.success(List(4) { movie(it + 1) })
        )
        moviePagingSource = MoviePagingSource(movieRepo)

        val page = Page(
            data = List(4) { movie(it + 1) },
            prevKey = null,
            nextKey = 2
        )
        val load = moviePagingSource.load(
            Refresh(
                key = null,
                loadSize = 4,
                placeholdersEnabled = false
            )
        )
        assertEquals(page, load)
    }

    private fun movie(id: Int) = Movie(
        id = id,
        title = "title$id",
        overview = "overview$id",
        listPosterUrl = "listPosterUrl$id",
        detailsPosterUrl = "detailsPosterUrl$id",
        averageVote = 9.9,
        genres = emptyList()
    )


}
package com.shakenbeer.neufilm.domain

import com.shakenbeer.neufilm.domain.entity.Actor
import com.shakenbeer.neufilm.domain.repo.ActorRepo
import com.shakenbeer.neufilm.domain.repo.MovieRepo
import javax.inject.Inject

class GetActorsUseCase @Inject constructor(
    private val actorRepo: ActorRepo,
    private val movieRepo: MovieRepo
) {
    suspend operator fun invoke(): List<Actor> = movieRepo.getSelectedMovie().run {
        if (isSuccess) {
            getOrNull()?.let { selected ->
                actorRepo.getActors(selected.id).getOrDefault(emptyList())
            } ?: emptyList()
        } else {
            emptyList()
        }
    }
}
package com.shakenbeer.neufilm.domain.repo

import com.shakenbeer.neufilm.domain.entity.Actor

interface ActorRepo {

    suspend fun getActors(movieId: Int): Result<List<Actor>>
}
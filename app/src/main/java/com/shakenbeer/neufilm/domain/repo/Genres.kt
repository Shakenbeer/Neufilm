package com.shakenbeer.neufilm.domain.repo

import com.shakenbeer.neufilm.domain.entity.Genre

interface Genres {
    suspend fun get(): List<Genre>
}
package com.shakenbeer.neufilm.domain.entity

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val listPosterUrl: String,
    val detailsPosterUrl: String,
    val averageVote: Double,
    val genres: List<Genre>
) {
    val genre1: String? = genres.getOrNull(0)?.name

    val genre2: String? = genres.getOrNull(1)?.name

    val rating: String = String.format("%.1f", averageVote)
}
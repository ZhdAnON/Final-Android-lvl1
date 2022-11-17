package ru.zhdanon.skillcinema.data

data class FilmFilterParams(
    val countries: String = "",
    val genres: String = "",
    val order: String = "RATING",
    val type: String = "",                          // "ALL"
    val ratingFrom: Int = 0,
    val ratingTo: Int = 10,
    val yearFrom: Int = 1000,
    val yearTo: Int = 3000,
    val imdbId: String? = null,
    val keyword: String = ""
)
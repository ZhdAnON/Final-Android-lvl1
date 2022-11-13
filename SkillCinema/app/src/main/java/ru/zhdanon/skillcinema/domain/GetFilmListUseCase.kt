package ru.zhdanon.skillcinema.domain

import ru.zhdanon.skillcinema.data.CinemaRepository
import ru.zhdanon.skillcinema.data.filmbyfilter.FilmByFilter
import javax.inject.Inject

class GetFilmListUseCase @Inject constructor(private val repository: CinemaRepository) {

    suspend fun executeFilmsByFilter(
        countries: String = "",
        genres: String = "",
        order: String = "YEAR",
        type: String = "",
        ratingFrom: Int = 0,
        ratingTo: Int = 10,
        yearFrom: Int = 1000,
        yearTo: Int = 3000,
        imdbId: String? = null,
        keyword: String = "",
        page: Int
    ): List<FilmByFilter> {
        return repository.getFilmsByFilter(
            countries = countries,
            genres = genres,
            order = order,
            type = type,
            ratingFrom = ratingFrom,
            ratingTo = ratingTo,
            yearFrom = yearFrom,
            yearTo = yearTo,
            imdbId = imdbId,
            keyword = keyword,
            page = page
        )
    }
}
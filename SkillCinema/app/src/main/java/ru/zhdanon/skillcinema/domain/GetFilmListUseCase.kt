package ru.zhdanon.skillcinema.domain

import ru.zhdanon.skillcinema.data.CinemaRepository
import ru.zhdanon.skillcinema.data.FilmFilterParams
import ru.zhdanon.skillcinema.data.filmbyfilter.FilmByFilter
import javax.inject.Inject

class GetFilmListUseCase @Inject constructor(private val repository: CinemaRepository) {

    suspend fun executeFilmsByFilter(
        filters: FilmFilterParams,
        page: Int
    ): List<FilmByFilter> {
        return repository.getFilmsByFilter(
            filters = filters,
            page = page
        )
    }
}
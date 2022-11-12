package ru.zhdanon.skillcinema.domain

import ru.zhdanon.skillcinema.data.CinemaRepository

class GetGalleryByIdUseCase(private val repository: CinemaRepository) {

    suspend fun executeGalleryByFilmId(filmId: Int, type: String, page: Int) =
        repository.getGalleryByFilmId(filmId, type, page)
}
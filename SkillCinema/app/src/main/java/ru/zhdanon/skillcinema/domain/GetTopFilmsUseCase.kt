package ru.zhdanon.skillcinema.domain

import ru.zhdanon.skillcinema.data.CinemaRepository
import ru.zhdanon.skillcinema.entity.HomeItem

class GetTopFilmsUseCase(private val cinemaRepository: CinemaRepository) {

    suspend fun executeTopFilms(topType: String, page: Int): List<HomeItem> {
        return cinemaRepository.getFilmsTop(topType, page)
    }
}
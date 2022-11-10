package ru.zhdanon.skillcinema.domain

import ru.zhdanon.skillcinema.data.CinemaRepository
import ru.zhdanon.skillcinema.entity.HomeItem

class GetPremierFilmUseCase(private val repository: CinemaRepository) {

    suspend fun executePremieres(year: Int, month: String): List<HomeItem> {
        return repository.getFilmsPremier(year, month)
    }
}
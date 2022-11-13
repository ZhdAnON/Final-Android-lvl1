package ru.zhdanon.skillcinema.domain

import ru.zhdanon.skillcinema.data.CinemaRepository
import ru.zhdanon.skillcinema.data.actorsbyfilmid.ResponseActorsByFilmId
import javax.inject.Inject

class GetActorsListUseCase @Inject constructor(private val repository: CinemaRepository) {

    suspend fun executeActorsList(filmId: Int): List<ResponseActorsByFilmId> {
        return repository.getActorsByFilmId(filmId)
    }
}
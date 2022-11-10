package ru.zhdanon.skillcinema

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.zhdanon.skillcinema.data.CinemaRepository

fun main() {
    val repository = CinemaRepository()
    runBlocking {
        launch(Dispatchers.IO) {
            val film = listOf(
                repository.getFilmById(329),
                repository.getFilmById(4477075),
                repository.getFilmById(5116673),
                repository.getFilmById(1297211),
                repository.getFilmById(4639557),
                repository.getFilmById(1405508)
            )

            film.forEach {
                println("${it.filmLength} | ${it.countries.joinToString("_")} | ${it.ratingAgeLimits}")
            }
        }
    }
}
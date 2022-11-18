package ru.zhdanon.skillcinema.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.zhdanon.skillcinema.data.filmbyfilter.FilmByFilter
import ru.zhdanon.skillcinema.data.filmspremier.FilmPremier
import ru.zhdanon.skillcinema.entity.HomeItem
import javax.inject.Inject

class CinemaRepository @Inject constructor() {
    private val retrofit: KinopoiskApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(KinopoiskApi::class.java)

    // FragmentFilmDetail
    suspend fun getFilmById(filmId: Int) = retrofit.getCurrentFilm(filmId)

    suspend fun getSeasonsById(seriesId: Int) = retrofit.getSeasons(seriesId)

    suspend fun getActorsByFilmId(filmId: Int) = retrofit.getActors(filmId)

    suspend fun getGalleryByFilmId(filmId: Int, type: String, page: Int) =
        retrofit.getFilmImages(filmId, type, page)

    suspend fun getSimilarByFilmId(filmId: Int) = retrofit.getSimilarFilms(filmId)

    // FragmentStaffDetail
    suspend fun getStaffById(staffId: Int) = retrofit.getStaff(staffId)

    // FragmentSearch
    suspend fun getFilmsByFilter(filters: ParamsFilterFilm, page: Int): List<FilmByFilter> {
        return retrofit.getFilmsByFilter(
            countries = "",
            genres = "",
            order = filters.order,
            type = filters.type,
            ratingFrom = filters.ratingFrom,
            ratingTo = filters.ratingTo,
            yearFrom = filters.yearFrom,
            yearTo = filters.yearTo,
            imdbId = filters.imdbId,
            keyword = filters.keyword,
            page = page
        ).items
    }

    suspend fun getFilmsTop(topType: String, page: Int): List<HomeItem> {
        return retrofit.getFilmsTop(type = topType, page = page).films
    }

    suspend fun getFilmsPremier(year: Int, month: String): List<FilmPremier> {
        return retrofit.getPremier(year, month).items
    }

    companion object {
        private const val BASE_URL = "https://kinopoiskapiunofficial.tech/api/"
    }
}
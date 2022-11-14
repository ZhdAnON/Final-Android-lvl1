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

    suspend fun getActorsByFilmId(filmId: Int) = retrofit.getActors(filmId)

    suspend fun getGalleryByFilmId(filmId: Int, type: String, page: Int) =
        retrofit.getFilmImages(filmId, type, page)

    suspend fun getSimilarByFilmId(filmId: Int) = retrofit.getSimilarFilms(filmId)

    // FragmentStaffDetail
    suspend fun getStaffById(staffId: Int) = retrofit.getStaff(staffId)

    // FragmentSearch
    suspend fun getFilmsByFilter(
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
        return retrofit.getFilmsByFilter(
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
        ).items
    }

    suspend fun getFilmsTop(topType: String, page: Int): List<HomeItem> {
        return retrofit.getFilmsTop(type = topType, page = page).films
    }

    suspend fun getFilmsPremier(
        year: Int,
        month: String
    ): List<FilmPremier> {
        return retrofit.getPremier(year, month).items
    }

    companion object {
        private const val BASE_URL = "https://kinopoiskapiunofficial.tech/api/"
    }
}
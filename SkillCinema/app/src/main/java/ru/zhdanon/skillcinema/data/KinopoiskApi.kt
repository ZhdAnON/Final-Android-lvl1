package ru.zhdanon.skillcinema.data

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.zhdanon.skillcinema.data.filmbyfilter.ResponseByFilter
import ru.zhdanon.skillcinema.data.filmbyid.ResponseCurrentFilm
import ru.zhdanon.skillcinema.data.filmspremier.ResponsePremier
import ru.zhdanon.skillcinema.data.filmstop.ResponseTop

interface KinopoiskApi {
    @Headers("X-API-KEY: $API_KEY")
    @GET("films/{id}")
    suspend fun getCurrentFilm(
        @Path("id") id: Int
    ): ResponseCurrentFilm

    @Headers("X-API-KEY: $API_KEY")
    @GET("films/top")
    suspend fun getFilmsTop(
        @Query("type") type: String,
        @Query("page") page: Int
    ): ResponseTop

    @Headers("X-API-KEY: $API_KEY")
    @GET("films/premieres")
    suspend fun getPremier(
        @Query("year") year: Int,
        @Query("month") month: String
    ): ResponsePremier

    @Headers("X-API-KEY: $API_KEY")
    @GET("films/")
    suspend fun getFilmsByFilter(
        @Query("countries") countries: String,
        @Query("genres") genres: String,
        @Query("order") order: String,
        @Query("type") type: String,
        @Query("ratingFrom") ratingFrom: Int,
        @Query("ratingTo") ratingTo: Int,
        @Query("yearFrom") yearFrom: Int,
        @Query("yearTo") yearTo: Int,
        @Query("imdbId") imdbId: String?,
        @Query("keyword") keyword: String,
        @Query("page") page: Int
    ): ResponseByFilter

    companion object {
        private const val API_KEY = "ffcd0204-2065-4214-b6ae-aa29f5fe4003"
//        private const val API_KEY_2 = "f746dfa5-8093-401b-8df2-e84042f3dc96"
    }
}
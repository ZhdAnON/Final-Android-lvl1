package ru.zhdanon.skillcinema.ui.filmdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.zhdanon.skillcinema.app.*
import ru.zhdanon.skillcinema.data.filmbyid.ResponseCurrentFilm
import ru.zhdanon.skillcinema.databinding.FragmentFilmDetailBinding
import ru.zhdanon.skillcinema.ui.CinemaViewModel
import ru.zhdanon.skillcinema.ui.StateLoading
import ru.zhdanon.skillcinema.ui.TAG

class FragmentFilmDetail : Fragment() {
    private var _binding: FragmentFilmDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CinemaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateLoadingListener()

        setFilmDetails()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun stateLoadingListener() {
        viewModel.loadCurrentFilmState.onEach { state ->
            Log.d(TAG, "stateLoadingListener: state = ${state.javaClass}")
            when (state) {
                is StateLoading.Loading -> {
                    binding.apply {
                        progressGroup.isVisible = true
                        loadingProgress.isVisible = true
                        loadingRefreshBtn.isVisible = false
                        filmMainGroup.isVisible = false
                        filmDescriptionGroup.isVisible = false
                    }
                }
                is StateLoading.Success -> {
                    binding.apply {
                        progressGroup.isVisible = false
                        filmMainGroup.isVisible = true
                        filmDescriptionGroup.isVisible = true
                    }
                }
                else -> {
                    binding.apply {
                        progressGroup.isVisible = true
                        loadingProgress.isVisible = false
                        loadingRefreshBtn.isVisible = true
                        filmMainGroup.isVisible = false
                        filmDescriptionGroup.isVisible = false
                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setFilmDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentFilm.collect { film ->
                Log.d(TAG, "Информации о фильме: ${film.kinopoiskId} | ${film.nameRu}")
                binding.apply {
                    filmPoster.loadImage(film.posterUrl)
                    filmDescriptionShort.text = film.shortDescription
                    filmDescriptionFull.text = film.description
                    filmRatingNameTv.text = getRatingName(film)
                    filmYearGenresTv.text = getYearGenres(film)
                    filmCountryLengthAgeLimitTv.text = getStrCountriesLengthAge(film)
                }
            }
        }
    }

    companion object {
        fun getRatingName(film: ResponseCurrentFilm): String {
            val result = mutableListOf<String>()
            val rating = when {
                film.ratingKinopoisk != null -> film.ratingKinopoisk.toString()
                film.ratingImdb != null -> film.ratingImdb.toString()
                film.ratingMpaa != null -> film.ratingMpaa.toString()
                else -> null
            }
            if (rating != null) result.add(rating)
            val name = when {
                film.nameRu != null -> film.nameRu
                film.nameEn != null -> film.nameEn
                film.nameOriginal != null -> film.nameOriginal
                else -> null
            }
            if (name != null) result.add(name)
            return result.joinToString(", ")
        }

        fun getYearGenres(film: ResponseCurrentFilm): String {
            val result = mutableListOf<String>()
            if (film.year != null) result.add(film.year.toString())
            if (film.genres.size > 1) {
                val genreNameList = mutableListOf<String>()
                repeat(2) {
                    genreNameList.add(film.genres[it].genre)
                }
                result.add(genreNameList.joinToString(", "))
            } else {
                result.add(film.genres[0].genre)
            }
            return result.joinToString(", ")
        }

        fun getStrCountriesLengthAge(film: ResponseCurrentFilm): String {
            val result = mutableListOf<String?>()
            result.add(film.getCountries())
            if (film.getLength() != null) result.add(film.getLength())
            if (film.getAgeLimit() != null) result.add("${film.getAgeLimit()}+")
            return result.joinToString(", ")
        }

        private fun ResponseCurrentFilm.getCountries(): String {
            return if(this.countries.size > 1) {
                val list = mutableListOf<String>()
                repeat(this.countries.size - 1) {
                    list.add(this.countries[it].country)
                }
                list.joinToString(", ")
            } else if (this.countries.size == 1) {
                this.countries[0].country
            } else {
                ""
            }
        }

        private fun ResponseCurrentFilm.getLength(): String? {
            return if (this.filmLength != null) {
                val hours = this.filmLength.div(60)
                val minutes = this.filmLength.rem(60)
                "$hours ч $minutes мин"
            } else null
        }

        private fun ResponseCurrentFilm.getAgeLimit(): String? {
            return if (this.ratingAgeLimits != null) this.ratingAgeLimits.removePrefix("age")
            else null
        }
    }
}
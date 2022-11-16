package ru.zhdanon.skillcinema.ui.filmdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.zhdanon.skillcinema.R
import ru.zhdanon.skillcinema.app.loadImage
import ru.zhdanon.skillcinema.data.filmbyid.ResponseCurrentFilm
import ru.zhdanon.skillcinema.data.staffbyfilmid.ResponseStaffByFilmId
import ru.zhdanon.skillcinema.databinding.FragmentFilmDetailBinding
import ru.zhdanon.skillcinema.ui.CinemaViewModel
import ru.zhdanon.skillcinema.ui.StateLoading
import ru.zhdanon.skillcinema.ui.allfilmsbycategory.FragmentAllFilms
import ru.zhdanon.skillcinema.ui.filmdetail.galleryadapter.GalleryAdapter
import ru.zhdanon.skillcinema.ui.filmdetail.staffadapter.StaffAdapter
import ru.zhdanon.skillcinema.ui.home.FragmentHome
import ru.zhdanon.skillcinema.ui.home.filmrecycler.FilmAdapter

class FragmentFilmDetail : Fragment() {
    private var _binding: FragmentFilmDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CinemaViewModel by activityViewModels()
    private lateinit var actorAdapter: StaffAdapter
    private lateinit var makersAdapter: StaffAdapter
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var similarAdapter: FilmAdapter

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

        val navArg: FragmentFilmDetailArgs by navArgs()

        stateLoadingListener()              // Установка слушателя состояния загрузки

        setFilmDetails()                    // Установка постера, инфорации на нём и описания фильма
        setFilmActors()                     // Установка списка актёров
        setFilmMakers()                     // Установка списка съёмочной группы
        setFilmGallery()                    // Установка галереи фотографий
        setSimilarFilms()                   // Установка списка похожих фильмов

        setButtonBackClick(navArg.keyNav)   // Установка навигации стрелкой "назад"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setButtonBackClick(navKey: String) {
        binding.btnBack.setOnClickListener {
            when (navKey) {
                FragmentHome.KEY_NAV_FRAGMENT_HOME ->
                    findNavController().navigate(
                        resId = R.id.fragmentHome,
                        args = null,
                        navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.fragmentHome, true)
                            .build()
                    )
                FragmentAllFilms.KEY_NAV_FRAGMENT_ALL_FILMS ->
                    findNavController().navigate(
                        resId = R.id.fragmentAllFilms,
                        args = null,
                        navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.fragmentAllFilms, true)
                            .build()
                    )
                else -> findNavController().navigate(
                    resId = R.id.fragmentHome,
                    args = null,
                    navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.fragmentHome, true)
                        .build()
                )
            }
        }
    }

    private fun stateLoadingListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loadCurrentFilmState.collect { state ->
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
            }
        }
    }

    // Информация о фильме
    private fun setFilmDetails() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.currentFilm.collect { film ->
                        if (film != null) {
                            binding.apply {
                                filmName.text = getName(film)
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
            }
        }
    }

    // Список актёров и съёмочной группы
    private fun setFilmActors() {
        actorAdapter = StaffAdapter { onStaffClick(it) }
        binding.filmActorsList.layoutManager =
            GridLayoutManager(
                requireContext(), MAX_ACTORS_ROWS, GridLayoutManager.HORIZONTAL, false
            )
        binding.filmActorsList.adapter = actorAdapter
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.currentFilmActors.collect { actorList ->
                binding.filmActorsCount.text = actorList.size.toString()
                if (actorList.size < MAX_ACTORS_COLUMN * MAX_ACTORS_ROWS) {
                    actorAdapter.submitList(actorList)
                } else {
                    val actorsListTemp = mutableListOf<ResponseStaffByFilmId>()
                    repeat(MAX_ACTORS_COLUMN * MAX_ACTORS_ROWS) { actorsListTemp.add(actorList[it]) }
                    actorAdapter.submitList(actorsListTemp)
                }
            }
        }
    }

    private fun setFilmMakers() {
        makersAdapter = StaffAdapter { onStaffClick(it) }
        binding.filmMakersList.layoutManager =
            GridLayoutManager(
                requireContext(),
                MAX_MAKERS_ROWS,
                GridLayoutManager.HORIZONTAL,
                false
            )
        binding.filmMakersList.adapter = makersAdapter

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.currentFilmMakers.collect { makersList ->
                if (makersList.size < MAX_MAKERS_COLUMN * MAX_MAKERS_ROWS) {
                    binding.filmMakersCount.isVisible = false
                    binding.filmMakersBtn.isVisible = false
                    makersAdapter.submitList(makersList)
                } else {
                    binding.filmMakersCount.isVisible = true
                    binding.filmMakersBtn.isVisible = true
                    binding.filmMakersCount.text = makersList.size.toString()
                    val makersListTemp = mutableListOf<ResponseStaffByFilmId>()
                    repeat(MAX_MAKERS_COLUMN * MAX_MAKERS_ROWS) { makersListTemp.add(makersList[it]) }
                    makersAdapter.submitList(makersListTemp)
                }
            }
        }
    }

    private fun onStaffClick(staff: ResponseStaffByFilmId) {
        val action =
            FragmentFilmDetailDirections.actionFragmentFilmDetailToFragmentStaffDetail(staff.staffId)
        findNavController().navigate(action)
    }

    // Галерея фильма
    private fun setFilmGallery() {
        binding.filmGalleryBtn.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentFilmDetail_to_fragmentGallery)
        }

        galleryAdapter = GalleryAdapter()
        binding.filmGalleryList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.filmGalleryList.adapter = galleryAdapter

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.currentFilmGallery.collect { responseGallery ->
                galleryAdapter.submitList(responseGallery)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.galleryCount.collect {
                binding.filmGalleryCount.text = it.toString()
            }
        }
    }

    // Похожие фильмы
    private fun setSimilarFilms() {
        similarAdapter = FilmAdapter(20, { }, { onSimilarFilmClick(it) })
        binding.filmSimilarList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.filmSimilarList.adapter = similarAdapter
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.currentFilmSimilar.collect {
                binding.filmSimilarCount.text = it.size.toString()
                similarAdapter.submitList(it)
            }
        }
    }

    private fun onSimilarFilmClick(filmId: Int) {
        viewModel.getFilmById(filmId)
    }

    companion object {
        private const val MAX_ACTORS_COLUMN = 5
        private const val MAX_ACTORS_ROWS = 4
        private const val MAX_MAKERS_COLUMN = 3
        private const val MAX_MAKERS_ROWS = 2

        private fun getRatingName(film: ResponseCurrentFilm): String {
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

        private fun getName(film: ResponseCurrentFilm): String {
            return when {
                film.nameRu != null -> film.nameRu
                film.nameEn != null -> film.nameEn
                film.nameOriginal != null -> film.nameOriginal
                else -> ""
            }
        }

        private fun getYearGenres(film: ResponseCurrentFilm): String {
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

        private fun getStrCountriesLengthAge(film: ResponseCurrentFilm): String {
            val result = mutableListOf<String?>()
            result.add(film.getCountries())
            if (film.getLength() != null) result.add(film.getLength())
            if (film.getAgeLimit() != null) result.add("${film.getAgeLimit()}+")
            return result.joinToString(", ")
        }

        private fun ResponseCurrentFilm.getCountries(): String {
            return if (this.countries.size > 1) {
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
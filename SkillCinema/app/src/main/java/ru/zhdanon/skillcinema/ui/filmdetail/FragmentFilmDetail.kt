package ru.zhdanon.skillcinema.ui.filmdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.zhdanon.skillcinema.R
import ru.zhdanon.skillcinema.app.loadImage
import ru.zhdanon.skillcinema.data.actorsbyfilmid.ResponseActorsByFilmId
import ru.zhdanon.skillcinema.data.filmbyid.ResponseCurrentFilm
import ru.zhdanon.skillcinema.data.filmgallery.ItemImageGallery
import ru.zhdanon.skillcinema.databinding.FragmentFilmDetailBinding
import ru.zhdanon.skillcinema.ui.CinemaViewModel
import ru.zhdanon.skillcinema.ui.StateLoading
import ru.zhdanon.skillcinema.ui.filmdetail.actorsadapter.ActorsAdapter
import ru.zhdanon.skillcinema.ui.filmdetail.galleryadapter.GalleryAdapter

class FragmentFilmDetail : Fragment() {
    private var _binding: FragmentFilmDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CinemaViewModel by activityViewModels()
    private lateinit var actorAdapter: ActorsAdapter
    private lateinit var makersAdapter: ActorsAdapter
    private lateinit var galleryAdapter: GalleryAdapter

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
        setFilmActors()
        setFilmMakers()
        setFilmGallery()

        binding.btnBack.setOnClickListener { findNavController().navigate(R.id.fragmentHome) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun stateLoadingListener() {
        viewModel.loadCurrentFilmState.onEach { state ->
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

    private fun setFilmActors() {
        actorAdapter = ActorsAdapter { onActorClick(it) }
        binding.filmActorsList.layoutManager =
            GridLayoutManager(
                requireContext(),
                MAX_ACTORS_ROWS,
                GridLayoutManager.HORIZONTAL,
                false
            )
        binding.filmActorsList.adapter = actorAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentFilmActors.collect { actorList ->
                if (actorList.size < MAX_ACTORS_COLUMN * MAX_ACTORS_ROWS) {
                    binding.filmActorsBtn.isVisible = false
                    binding.filmActorsCount.isVisible = false
                    actorAdapter.submitList(actorList)
                } else {
                    binding.filmActorsBtn.isVisible = true
                    binding.filmActorsCount.isVisible = true
                    binding.filmActorsCount.text = actorList.size.toString()
                    val actorsListTemp = mutableListOf<ResponseActorsByFilmId>()
                    repeat(MAX_ACTORS_COLUMN * MAX_ACTORS_ROWS) { actorsListTemp.add(actorList[it]) }
                    actorAdapter.submitList(actorsListTemp)
                }
            }
        }
    }

    private fun setFilmMakers() {
        makersAdapter = ActorsAdapter { onActorClick(it) }
        binding.filmMakersList.layoutManager =
            GridLayoutManager(
                requireContext(),
                MAX_MAKERS_ROWS,
                GridLayoutManager.HORIZONTAL,
                false
            )
        binding.filmMakersList.adapter = makersAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentFilmMakers.collect { makersList ->
                if (makersList.size < MAX_MAKERS_COLUMN * MAX_MAKERS_ROWS) {
                    binding.filmMakersCount.isVisible = false
                    binding.filmMakersBtn.isVisible = false
                    makersAdapter.submitList(makersList)
                } else {
                    binding.filmMakersCount.isVisible = true
                    binding.filmMakersBtn.isVisible = true
                    binding.filmMakersCount.text = makersList.size.toString()
                    val makersListTemp = mutableListOf<ResponseActorsByFilmId>()
                    repeat(MAX_MAKERS_COLUMN * MAX_MAKERS_ROWS) { makersListTemp.add(makersList[it]) }
                    makersAdapter.submitList(makersListTemp)
                }
            }
        }
    }

    private fun setFilmGallery() {
        galleryAdapter = GalleryAdapter { }
        binding.filmGalleryList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.filmGalleryList.adapter = galleryAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentFilmGallery.collect { imageList ->
                if (imageList.size <= MAX_GALLERY_SIZE) {
                    binding.filmGalleryCount.isVisible = false
                    binding.filmGalleryBtn.isVisible = false
                    galleryAdapter.submitList(imageList)
                } else {
                    binding.filmGalleryBtn.isVisible = true
                    binding.filmGalleryCount.isVisible = true
                    binding.filmGalleryCount.text = imageList.size.toString()
                    val tempGallery = mutableListOf<ItemImageGallery>()
                    repeat(MAX_GALLERY_SIZE) {
                        tempGallery.add(imageList[it])
                    }
                    galleryAdapter.submitList(tempGallery)
                }
            }
        }
    }

    private fun onActorClick(actor: ResponseActorsByFilmId) {
        Toast.makeText(
            requireContext(),
            actor.nameRu ?: actor.nameEn ?: "Unknown name",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        private const val MAX_ACTORS_COLUMN = 5
        private const val MAX_ACTORS_ROWS = 4
        private const val MAX_MAKERS_COLUMN = 3
        private const val MAX_MAKERS_ROWS = 2
        private const val MAX_GALLERY_SIZE = 20

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
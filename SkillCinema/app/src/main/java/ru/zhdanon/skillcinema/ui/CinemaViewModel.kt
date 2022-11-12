package ru.zhdanon.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.zhdanon.skillcinema.app.converterInMonth
import ru.zhdanon.skillcinema.app.prepareToShow
import ru.zhdanon.skillcinema.data.CategoriesFilms
import ru.zhdanon.skillcinema.data.CinemaRepository
import ru.zhdanon.skillcinema.data.TOP_TYPES
import ru.zhdanon.skillcinema.data.actorsbyfilmid.ResponseActorsByFilmId
import ru.zhdanon.skillcinema.data.filmbyid.ResponseCurrentFilm
import ru.zhdanon.skillcinema.data.filmgallery.ItemImageGallery
import ru.zhdanon.skillcinema.domain.*
import ru.zhdanon.skillcinema.entity.HomeItem
import java.util.*

class CinemaViewModel : ViewModel() {

    private val repository = CinemaRepository()
    private val getTopFilmsUseCase = GetTopFilmsUseCase(repository)
    private val getPremierFilmUseCase = GetPremierFilmUseCase(repository)
    private val getFilmByIdUseCase = GetFilmByIdUseCase(repository)
    private val getActorsByFilmIdUseCase = GetActorsListUseCase(repository)
    private val getGalleryByIdUseCase = GetGalleryByIdUseCase(repository)

    private val calendar = Calendar.getInstance()

    private val _homePageList =
        MutableStateFlow<List<HomeList>>(emptyList())
    val homePageList = _homePageList.asStateFlow()

    private val _currentFilm = MutableSharedFlow<ResponseCurrentFilm>()
    val currentFilm = _currentFilm.asSharedFlow()

    private val _currentFilmActors = MutableStateFlow<List<ResponseActorsByFilmId>>(emptyList())
    val currentFilmActors = _currentFilmActors.asStateFlow()

    private val _currentFilmMakers = MutableStateFlow<List<ResponseActorsByFilmId>>(emptyList())
    val currentFilmMakers = _currentFilmMakers.asStateFlow()

    private val _currentFilmGallery = MutableStateFlow<List<ItemImageGallery>>(emptyList())
    val currentFilmGallery = _currentFilmGallery.asStateFlow()

    private val _loadCategoryState = MutableStateFlow<StateLoading>(StateLoading.Default)
    val loadCategoryState = _loadCategoryState.asStateFlow()

    private val _loadCurrentFilmState = MutableStateFlow<StateLoading>(StateLoading.Default)
    val loadCurrentFilmState = _loadCurrentFilmState.asStateFlow()

    init {
        getAllFilms()
    }

    fun getAllFilms(
        year: Int = calendar.get(Calendar.YEAR),
        month: String = (calendar.get(Calendar.MONTH) + 1).converterInMonth()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loadCategoryState.value = StateLoading.Loading
                _homePageList.value = listOf(
                    HomeList(
                        category = CategoriesFilms.BEST,
                        filmList = getTopFilmsUseCase.executeTopFilms(
                            topType = TOP_TYPES.getValue(CategoriesFilms.BEST),
                            page = 1
                        )
                    ),
                    HomeList(
                        category = CategoriesFilms.PREMIERS,
                        filmList = getPremierFilmUseCase.executePremieres(
                            year = year,
                            month = month
                        ).prepareToShow(20)
                    ),
                    HomeList(
                        category = CategoriesFilms.AWAIT,
                        filmList = getTopFilmsUseCase.executeTopFilms(
                            topType = TOP_TYPES.getValue(CategoriesFilms.AWAIT),
                            page = 1
                        )
                    ),
                    HomeList(
                        category = CategoriesFilms.POPULAR,
                        filmList = getTopFilmsUseCase.executeTopFilms(
                            topType = TOP_TYPES.getValue(CategoriesFilms.POPULAR),
                            page = 1
                        )
                    )
                )
                _loadCategoryState.value = StateLoading.Success
            } catch (e: Throwable) {
                _loadCategoryState.value = StateLoading.Error(e.message.toString())
            }
        }
    }

    fun getFilmById(filmId: Int) {
        viewModelScope.launch {
            try {
                _loadCurrentFilmState.value = StateLoading.Loading
                val tempFilm = getFilmByIdUseCase.executeFilmById(filmId)
                _currentFilm.emit(tempFilm)
                val tempActorList = getActorsByFilmIdUseCase.executeActorsList(filmId)
                _currentFilmGallery.value = getGalleryByIdUseCase
                    .executeGalleryByFilmId(filmId, "SCREENSHOT", 1).items
                sortingActorsAndMakers(tempActorList)
                _loadCurrentFilmState.value = StateLoading.Success
            } catch (e: Throwable) {
                _loadCurrentFilmState.value = StateLoading.Error(e.message.toString())
            }
        }
    }

    private fun sortingActorsAndMakers(actorsList: List<ResponseActorsByFilmId>) {
        val actors = mutableListOf<ResponseActorsByFilmId>()
        val makers = mutableListOf<ResponseActorsByFilmId>()
        actorsList.forEach {
            if (it.professionKey == "ACTOR") actors.add(it)
            else makers.add(it)
        }
        _currentFilmActors.value = actors
        _currentFilmMakers.value = makers
    }

    companion object {
        data class HomeList(
            val category: CategoriesFilms,
            val filmList: List<HomeItem>
        )
    }
}
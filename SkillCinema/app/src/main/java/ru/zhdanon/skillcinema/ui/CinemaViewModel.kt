package ru.zhdanon.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.zhdanon.skillcinema.app.converterInMonth
import ru.zhdanon.skillcinema.app.prepareToShow
import ru.zhdanon.skillcinema.data.CategoriesFilms
import ru.zhdanon.skillcinema.data.GALLERY_TYPES
import ru.zhdanon.skillcinema.data.TOP_TYPES
import ru.zhdanon.skillcinema.data.filmbyid.ResponseCurrentFilm
import ru.zhdanon.skillcinema.data.filmgallery.ItemImageGallery
import ru.zhdanon.skillcinema.data.similarfilm.SimilarItem
import ru.zhdanon.skillcinema.data.staffbyfilmid.ResponseStaffByFilmId
import ru.zhdanon.skillcinema.domain.*
import ru.zhdanon.skillcinema.entity.HomeItem
import ru.zhdanon.skillcinema.ui.allfilmsbycategory.allfilmadapter.AllFilmAdapter
import ru.zhdanon.skillcinema.ui.allfilmsbycategory.allfilmadapter.AllFilmPagingSource
import ru.zhdanon.skillcinema.ui.gallery.recycleradapter.GalleryFullPagingSource
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CinemaViewModel @Inject constructor(
    private val getTopFilmsUseCase: GetTopFilmsUseCase,
    private val getPremierFilmUseCase: GetPremierFilmUseCase,
    private val getFilmByIdUseCase: GetFilmByIdUseCase,
    private val getActorsByFilmIdUseCase: GetActorsListUseCase,
    private val getGalleryByIdUseCase: GetGalleryByIdUseCase,
    private val getSimilarFilmsUseCase: GetSimilarFilmsUseCase
) : ViewModel() {
    private val calendar = Calendar.getInstance()

    private var filmId = 328

    // FragmentHome
    private val _homePageList =
        MutableStateFlow<List<HomeList>>(emptyList())
    val homePageList = _homePageList.asStateFlow()

    private val _loadCategoryState = MutableStateFlow<StateLoading>(StateLoading.Default)
    val loadCategoryState = _loadCategoryState.asStateFlow()

    // FragmentAllFilms
    private var allFilmAdapter: AllFilmAdapter = AllFilmAdapter { }
    private lateinit var currentCategory: CategoriesFilms

    val allFilmsByCategory: Flow<PagingData<HomeItem>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            AllFilmPagingSource(
                categoriesFilms = currentCategory,
                year = calendar.get(Calendar.YEAR),
                month = (calendar.get(Calendar.MONTH) + 1).converterInMonth(),
                getPremierFilmUseCase, getTopFilmsUseCase
            )
        }
    ).flow.cachedIn(viewModelScope)

    // FragmentFilmDetail
    private val _currentFilm = MutableStateFlow<ResponseCurrentFilm?>(null)
    val currentFilm = _currentFilm.asStateFlow()

    private val _currentFilmActors = MutableStateFlow<List<ResponseStaffByFilmId>>(emptyList())
    val currentFilmActors = _currentFilmActors.asStateFlow()

    private val _currentFilmMakers = MutableStateFlow<List<ResponseStaffByFilmId>>(emptyList())
    val currentFilmMakers = _currentFilmMakers.asStateFlow()

    private val _currentFilmGallery = MutableStateFlow<List<ItemImageGallery>>(emptyList())
    val currentFilmGallery = _currentFilmGallery.asStateFlow()

    private val _currentFilmSimilar = MutableStateFlow<List<SimilarItem>>(emptyList())
    val currentFilmSimilar = _currentFilmSimilar.asStateFlow()

    private val _loadCurrentFilmState = MutableStateFlow<StateLoading>(StateLoading.Default)
    val loadCurrentFilmState = _loadCurrentFilmState.asStateFlow()

    init {
        getFilmsByCategories()
    }

    // FragmentHome
    fun getFilmsByCategories(
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

    fun setCurrentCategory(category: CategoriesFilms) {
        currentCategory = category
        if (allFilmAdapter.itemCount != 0) allFilmAdapter.refresh()
    }

    fun getCurrentCategory() = currentCategory

    // FragmentAllFilms
    fun getAllFilmAdapter() = allFilmAdapter
    fun setAllFilmAdapter(adapter: AllFilmAdapter) {
        allFilmAdapter = adapter
    }

    // FragmentFilmDetail
    fun getFilmById(filmId: Int) {
        this.filmId = filmId
        viewModelScope.launch {
            try {
                _loadCurrentFilmState.value = StateLoading.Loading
                // film
                val tempFilm = getFilmByIdUseCase.executeFilmById(filmId)
                _currentFilm.value = tempFilm
                // staffs
                val tempActorList = getActorsByFilmIdUseCase.executeActorsList(filmId)
                sortingActorsAndMakers(tempActorList)
                // gallery
                setGalleryCount(filmId)
                _currentFilmGallery.value =
                    getGalleryByIdUseCase.executeGalleryByFilmId(filmId, "STILL", 1).items
                // similar
                val responseSimilar = getSimilarFilmsUseCase.executeSimilarFilms(filmId)
                if (responseSimilar.total != 0) _currentFilmSimilar.value = responseSimilar.items!!
                _loadCurrentFilmState.value = StateLoading.Success
            } catch (e: Throwable) {
                _loadCurrentFilmState.value = StateLoading.Error(e.message.toString())
            }
        }
    }

    private val _galleryCount = MutableStateFlow(0)
    val galleryCount = _galleryCount.asStateFlow()

    private val _galleryChipList = MutableStateFlow<Map<String, Int>>(emptyMap())
    val galleryChipList = _galleryChipList.asStateFlow()

    private fun setGalleryCount(filmId: Int) {
        _galleryCount.value = 0
        val tempChipsList = mutableMapOf<String, Int>()
        var countImages = 0
        viewModelScope.launch(Dispatchers.IO) {
            GALLERY_TYPES.forEach {
                val temp = getGalleryByIdUseCase.executeGalleryByFilmId(filmId, it.key, 1)
                tempChipsList[it.key] = temp.total
                countImages += temp.total
            }
            _galleryCount.value = countImages
            _galleryChipList.value = tempChipsList
        }
    }

    // FragmentGallery
    private var galleryType: String = "STILL"

    fun setGalleryType(type: String) {
        if (GALLERY_TYPES.keys.contains(type)) galleryType = type
    }

    val galleryByType: Flow<PagingData<ItemImageGallery>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            GalleryFullPagingSource(
                getGalleryByIdUseCase = getGalleryByIdUseCase,
                filmId = filmId,
                galleryType = galleryType
            )
        }
    ).flow.cachedIn(viewModelScope)

    private fun sortingActorsAndMakers(actorsList: List<ResponseStaffByFilmId>) {
        val actors = mutableListOf<ResponseStaffByFilmId>()
        val makers = mutableListOf<ResponseStaffByFilmId>()
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
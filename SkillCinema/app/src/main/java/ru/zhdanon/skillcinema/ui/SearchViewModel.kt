package ru.zhdanon.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import ru.zhdanon.skillcinema.data.ParamsFilterFilm
import ru.zhdanon.skillcinema.domain.GetFilmListUseCase
import ru.zhdanon.skillcinema.entity.HomeItem
import ru.zhdanon.skillcinema.ui.filmsbyfilter.FilmsByFilterPagingSource
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getFilmListUseCase: GetFilmListUseCase
) : ViewModel() {

    private var filters = ParamsFilterFilm()

    val films: Flow<PagingData<HomeItem>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            FilmsByFilterPagingSource(
                filters = filters,
                getFilmListUseCase = getFilmListUseCase
            )
        }
    ).flow.cachedIn(viewModelScope)

    fun updateFilters(filterFilm: ParamsFilterFilm) {
        filters = filterFilm
    }
}
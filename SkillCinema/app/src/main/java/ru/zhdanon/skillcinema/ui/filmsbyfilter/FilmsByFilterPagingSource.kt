package ru.zhdanon.skillcinema.ui.filmsbyfilter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.zhdanon.skillcinema.data.FilmFilterParams
import ru.zhdanon.skillcinema.domain.GetFilmListUseCase
import ru.zhdanon.skillcinema.entity.HomeItem

class FilmsByFilterPagingSource(
    private val filters: FilmFilterParams,
    private val getFilmListUseCase: GetFilmListUseCase
) : PagingSource<Int, HomeItem>() {
    override fun getRefreshKey(state: PagingState<Int, HomeItem>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomeItem> {
        val page = params.key ?: FIRST_PAGE
        return kotlin.runCatching {
            getFilmListUseCase
                .executeFilmsByFilter(
                    filters = filters,
                    page = page
                )
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it,
                    prevKey = null,
                    nextKey = if (it.isEmpty()) null else page + 1
                )
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}
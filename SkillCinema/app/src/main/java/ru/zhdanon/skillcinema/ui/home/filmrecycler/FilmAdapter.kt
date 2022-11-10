package ru.zhdanon.skillcinema.ui.home.filmrecycler

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.zhdanon.skillcinema.databinding.ItemFilmBinding
import ru.zhdanon.skillcinema.entity.HomeItem
import ru.zhdanon.skillcinema.ui.TAG

class FilmAdapter(
    private val maxListSize: Int,
    private val clickNextButton: () -> Unit,
    private val clickFilms: (filmId: Int) -> Unit
) : ListAdapter<HomeItem, FilmViewHolder>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FilmViewHolder(
        ItemFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        if (position == maxListSize - 1) {
            Log.d(TAG, "positionNext = $position, maxListSize = $maxListSize, ${getItem(position).nameRu}")
            holder.bindNextShow { clickNextButton() }
        }
        else {
            holder.bindItem(getItem(position)) { clickFilms(it) }
        }
    }
}
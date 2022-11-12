package ru.zhdanon.skillcinema.ui.filmdetail.actorsadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.zhdanon.skillcinema.data.actorsbyfilmid.ResponseActorsByFilmId
import ru.zhdanon.skillcinema.databinding.ItemActorFilmDetailBinding

class ActorsAdapter(
    private val clickActor: (filmId: ResponseActorsByFilmId) -> Unit
) : ListAdapter<ResponseActorsByFilmId, ActorViewHolder>(DiffActor()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ActorViewHolder(
        ItemActorFilmDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        holder.bindItem(getItem(position)) { clickActor(getItem(position)) }
    }
}
package ru.zhdanon.skillcinema.ui.filmdetail.actorsadapter

import androidx.recyclerview.widget.RecyclerView
import ru.zhdanon.skillcinema.app.loadImage
import ru.zhdanon.skillcinema.data.actorsbyfilmid.ResponseActorsByFilmId
import ru.zhdanon.skillcinema.databinding.ItemActorFilmDetailBinding

class ActorViewHolder(
    private val binding: ItemActorFilmDetailBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(item: ResponseActorsByFilmId, clickActor: (actor: ResponseActorsByFilmId) -> Unit) {
        binding.apply {
            actorAvatarFilmDetail.loadImage(item.posterUrl)
            actorNameFilmDetail.text = item.nameRu ?: item.nameEn ?: "Не указан"
            actorRoleFilmDetail.text = item.description ?: "Не указан"
        }
        binding.root.setOnClickListener { clickActor(item) }
    }
}
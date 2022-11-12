package ru.zhdanon.skillcinema.ui.filmdetail.actorsadapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import ru.zhdanon.skillcinema.data.actorsbyfilmid.ResponseActorsByFilmId

class DiffActor : DiffUtil.ItemCallback<ResponseActorsByFilmId>() {
    override fun areItemsTheSame(oldItem: ResponseActorsByFilmId, newItem: ResponseActorsByFilmId) =
        oldItem.staffId == newItem.staffId &&
                oldItem.description == newItem.description &&
                oldItem.professionText == newItem.professionText

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: ResponseActorsByFilmId,
        newItem: ResponseActorsByFilmId
    ) = oldItem == newItem
}
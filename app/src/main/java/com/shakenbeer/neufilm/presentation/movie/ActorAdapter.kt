package com.shakenbeer.neufilm.presentation.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.shakenbeer.neufilm.databinding.ItemActorBinding
import com.shakenbeer.neufilm.domain.entity.Actor

class ActorAdapter(private val onClick: (Actor) -> Unit) :
    ListAdapter<Actor, ActorViewHolder>(ActorDiffCallback()) {
    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        getItem(position)?.let { bind(holder, it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ActorViewHolder(
        ItemActorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) { getItem(it)?.let { actor -> onClick(actor) } }
}

private fun bind(holder: ActorViewHolder, actor: Actor) {
    with(holder.binding) {
        actorImageView.load(actor.profileUrl)
    }
}

class ActorDiffCallback : DiffUtil.ItemCallback<Actor>() {
    override fun areItemsTheSame(oldItem: Actor, newItem: Actor) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Actor, newItem: Actor) =
        oldItem.profileUrl == newItem.profileUrl
}

class ActorViewHolder(
    val binding: ItemActorBinding,
    private val clickListener: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener { clickListener(absoluteAdapterPosition) }
    }
}
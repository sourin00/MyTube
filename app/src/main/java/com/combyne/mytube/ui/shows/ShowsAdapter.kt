package com.combyne.mytube.ui.shows

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.combyne.mytube.data.Show
import com.combyne.mytube.databinding.CardItemShowBinding

class ShowsAdapter : ListAdapter<Show, ShowsAdapter.ShowsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowsViewHolder {
        val binding =
            CardItemShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowsViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ShowsViewHolder(private val binding: CardItemShowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(show: Show) {
            binding.apply {
                layout.txtShowName.text = show.name
                layout.txtRelease.text = show.releaseDateFormatted
                layout.txtSeasons.text = show.seasons.toInt().toString()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Show>() {
        override fun areItemsTheSame(oldItem: Show, newItem: Show): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Show, newItem: Show): Boolean =
            oldItem == newItem
    }
}
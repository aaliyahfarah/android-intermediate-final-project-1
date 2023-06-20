package com.bangkit.storyappdicoding.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyappdicoding.data.Story
import com.bangkit.storyappdicoding.databinding.StoryItemBinding

class StoryAdapters(private val onClickListener: OnClickListener) :
    ListAdapter<Story, StoryAdapters.StoryViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Story>() {

        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(
            StoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(story)
        }
        holder.bind(story)
    }

    class StoryViewHolder(private var binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            binding.story = story
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (story: Story) -> Unit) {
        fun onClick(story: Story) = clickListener(story)
    }

}

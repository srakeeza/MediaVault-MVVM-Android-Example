package com.example.mediavault.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediavault.databinding.ItemMediaBinding
import com.example.mediavault.model.MediaItem

class MediaAdapter(
    private val isVideo: Boolean
) : ListAdapter<MediaItem, MediaAdapter.MediaViewHolder>(MediaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(getItem(position), isVideo)
    }

    class MediaViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MediaItem, isVideo: Boolean) {
            Glide.with(binding.root.context)
                .load(item.uri)
                .centerCrop()
                .into(binding.imageView)

            binding.videoIndicator.visibility = if (isVideo) ViewGroup.VISIBLE else ViewGroup.GONE
            binding.textName.text = item.name
            binding.textDate.text = item.date
        }
    }

    private class MediaDiffCallback : DiffUtil.ItemCallback<MediaItem>() {
        override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            // Check if items are the same instance (usually by ID)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            // Check if all content is the same
            return oldItem == newItem
        }
    }
}

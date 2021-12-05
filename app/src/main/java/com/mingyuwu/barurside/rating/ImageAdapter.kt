package com.mingyuwu.barurside.rating

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.databinding.ItemRatingImageBinding

class ImageAdapter(val width: Int, val height: Int) :
    ListAdapter<Any, RecyclerView.ViewHolder>(ImageAdapter) {

    class ImageViewHolder(private var binding: ItemRatingImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(img: String, width: Int, height: Int) {
            binding.img = img
            binding.width = width
            binding.height = height
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of image url has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return true
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewHolder {

        return ImageViewHolder(
            ItemRatingImageBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                holder.bind((getItem(position) as String), width, height)
            }
        }
    }
}

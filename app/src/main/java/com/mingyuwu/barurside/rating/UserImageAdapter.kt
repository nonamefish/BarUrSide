package com.mingyuwu.barurside.rating

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.collect.TAG
import com.mingyuwu.barurside.databinding.ItemUserImageBinding

class UserImageAdapter(private val size: Int) :
    ListAdapter<String, UserImageAdapter.TagFrdViewHolder>(DiffCallback) {

    class TagFrdViewHolder(private var binding: ItemUserImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(img: String, size: Int) {
            binding.img = img
            binding.size = size
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagFrdViewHolder {

        return TagFrdViewHolder(
            ItemUserImageBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TagFrdViewHolder, position: Int) {
        val img = getItem(position)
        holder.bind(img, size)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount : ${super.getItemCount()}")
        return super.getItemCount()
    }

}
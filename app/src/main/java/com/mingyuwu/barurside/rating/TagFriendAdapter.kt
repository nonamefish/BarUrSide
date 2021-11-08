package com.mingyuwu.barurside.rating

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.collect.TAG
import com.mingyuwu.barurside.data.TagFriend
import com.mingyuwu.barurside.databinding.ItemTagFrdBinding

class TagFriendAdapter() :
    ListAdapter<TagFriend, TagFriendAdapter.TagFrdViewHolder>(DiffCallback) {

    class TagFrdViewHolder(private var binding: ItemTagFrdBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tagFriend: TagFriend) {
            binding.name = tagFriend.name

        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<TagFriend>() {
        override fun areItemsTheSame(oldItem: TagFriend, newItem: TagFriend): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: TagFriend, newItem: TagFriend): Boolean {
            return oldItem.id == newItem.id
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagFrdViewHolder {

        return TagFrdViewHolder(
            ItemTagFrdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TagFrdViewHolder, position: Int) {
        val tagFriend = getItem(position)
        holder.bind(tagFriend)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount : ${super.getItemCount()}")
        return super.getItemCount()
    }

}
package com.mingyuwu.barurside.rating

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.data.TagFriend
import com.mingyuwu.barurside.databinding.ItemTagFrdBinding

class TagFriendAdapter :
    ListAdapter<TagFriend, TagFriendAdapter.TagFrdViewHolder>(DiffCallback) {

    class TagFrdViewHolder(private var binding: ItemTagFrdBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): TagFrdViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTagFrdBinding.inflate(layoutInflater, parent, false)

                return TagFrdViewHolder(binding)
            }
        }

        fun bind(tagFriend: TagFriend, view: View) {
            binding.txtRtgUser.text = tagFriend.name
            binding.cardProfileBaseImg.setOnClickListener {
                view.findNavController().navigate(
                    MainNavigationDirections.navigateToProfileFragment(tagFriend.id)
                )
            }
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [TagFriend] has been updated.
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

        return TagFrdViewHolder.from(parent)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TagFrdViewHolder, position: Int) {
        val tagFriend = getItem(position)
        holder.bind(tagFriend, holder.itemView)
    }
}

package com.mingyuwu.barurside.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.databinding.ItemFriendBinding

class FriendAdapter(val viewModel: ViewModel) :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    class FriendViewHolder(private var binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: User?, viewModel: ViewModel) {
            binding.friend = friend
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (oldItem as User).id == (newItem as User).id
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendViewHolder {
        return FriendViewHolder(
            ItemFriendBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FriendViewHolder -> {
                holder.bind((getItem(position) as User), viewModel)
            }
        }
    }
}
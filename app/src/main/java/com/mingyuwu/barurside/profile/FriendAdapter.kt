package com.mingyuwu.barurside.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.databinding.ItemFriendBinding

class FriendAdapter(val viewModel: ViewModel) :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    class FriendViewHolder(private var binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: User?, view: View) {
            binding.friend = friend
            binding.profileBaseImg.setOnClickListener {
                friend?.let {
                    view.findNavController()
                        .navigate(MainNavigationDirections.navigateToProfileFragment(friend.id))
                }
            }
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [User] has been updated.
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
            ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FriendViewHolder -> {
                holder.bind((getItem(position) as User), holder.itemView)
            }
        }
    }
}

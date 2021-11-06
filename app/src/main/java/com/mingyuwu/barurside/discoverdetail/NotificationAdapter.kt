package com.mingyuwu.barurside.discoverdetail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.activity.ActivityPageViewModel
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.databinding.ItemDiscoverObjectBinding
import com.mingyuwu.barurside.databinding.ItemNotificationBinding
import com.mingyuwu.barurside.login.UserManager

class NotificationAdapter(val viewModel: DiscoverDetailViewModel) :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    class NotificationViewHolder(private var binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): NotificationViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemNotificationBinding.inflate(layoutInflater, parent, false)

                return NotificationViewHolder(binding)
            }
        }

        fun bind(notification: Notification, viewModel: DiscoverDetailViewModel) {
            binding.notification = notification
            binding.viewModel = viewModel
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (oldItem as Notification).id == (newItem as Notification).id
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {

        return NotificationViewHolder.from(parent)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NotificationViewHolder -> {
                Log.d("Ming", getItem(position).toString())
                holder.bind((getItem(position) as Notification), viewModel)
            }
        }
    }

}
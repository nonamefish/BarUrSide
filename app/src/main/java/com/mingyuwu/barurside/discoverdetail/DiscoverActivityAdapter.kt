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
import com.mingyuwu.barurside.databinding.ItemDiscoverActivityBinding
import com.mingyuwu.barurside.databinding.ItemDiscoverObjectBinding

class DiscoverActivityAdapter(val viewModel: ViewModel) :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    class DiscoverActivityViewHolder(private var binding: ItemDiscoverActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): DiscoverActivityViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDiscoverActivityBinding.inflate(layoutInflater, parent, false)

                return DiscoverActivityViewHolder(binding)
            }
        }

        fun bind(activity: Activity, viewModel: ViewModel) {
            when(viewModel){
                is DiscoverDetailViewModel->{
                    binding.btnObjectInfo.setOnClickListener {
                        viewModel.navigateToInfo.value = activity
                    }
                }
                is ActivityPageViewModel ->{
                    binding.btnObjectInfo.setOnClickListener {
                        viewModel.navigateToDetail.value = activity
                    }
                }
            }
            binding.name = activity.name
            binding.img = ""
            binding.category = activity.mainDrinking
            binding.info = activity.address
            binding.info2 = "上限: ${activity.peopleLimit} 人"


        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (oldItem as Activity).id == (newItem as Activity).id
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiscoverActivityViewHolder {

        return DiscoverActivityViewHolder.from(parent)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DiscoverActivityViewHolder -> {
                Log.d("Ming", getItem(position).toString())
                holder.bind((getItem(position) as Activity), viewModel)
            }
        }
    }

}
package com.mingyuwu.barurside.discoverdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.databinding.ItemDiscoverObjectBinding
import com.mingyuwu.barurside.util.Style

class DiscoverVenueAdapter(val viewModel: DiscoverDetailViewModel) :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    class DiscoverVenueViewHolder(private var binding: ItemDiscoverObjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): DiscoverVenueViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDiscoverObjectBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )

                return DiscoverVenueViewHolder(binding)
            }
        }

        fun bind(venue: Venue, viewModel: DiscoverDetailViewModel) {
            binding.name = venue.name
            binding.img = venue.images?.get(0) ?: ""
            binding.category = Style.valueOf(venue.style.uppercase()).chinese
            binding.info = venue.address
            binding.rating = binding.root.context.getString(
                R.string.venue_rating_info_list, venue.avgRating, venue.rtgCount
            )

            binding.cnstrtListItem.setOnClickListener {
                viewModel.navigateToInfo.value = venue
            }
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Venue] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (oldItem as Venue).id == (newItem as Venue).id
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiscoverVenueViewHolder {

        return DiscoverVenueViewHolder.from(parent)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DiscoverVenueViewHolder -> {
                holder.bind((getItem(position) as Venue), viewModel)
            }
        }
    }
}

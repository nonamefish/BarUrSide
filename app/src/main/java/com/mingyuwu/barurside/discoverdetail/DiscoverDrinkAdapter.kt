package com.mingyuwu.barurside.discoverdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.databinding.ItemDiscoverObjectBinding
import com.mingyuwu.barurside.util.Category

class DiscoverDrinkAdapter(val viewModel: DiscoverDetailViewModel) :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    class DiscoverDrinkViewHolder(private var binding: ItemDiscoverObjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): DiscoverDrinkViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDiscoverObjectBinding.inflate(layoutInflater, parent, false)

                return DiscoverDrinkViewHolder(binding)
            }
        }

        fun bind(drink: Drink, viewModel: DiscoverDetailViewModel) {
            binding.name = drink.name
            binding.img = drink.images?.get(0) ?: ""
            binding.category = Category.valueOf(drink.category.uppercase()).chinese
            binding.info = binding.root.context.getString(R.string.drink_info_price, drink.price)
            binding.rating = binding.root.context.getString(
                R.string.venue_rating_info_list, drink.avgRating, drink.rtgCount
            )

            binding.cnstrtListItem.setOnClickListener {
                viewModel.navigateToInfo.value = drink
            }
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Drink] has been updated.
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
    ): DiscoverDrinkViewHolder {

        return DiscoverDrinkViewHolder.from(parent)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DiscoverDrinkViewHolder -> {
                holder.bind((getItem(position) as Drink), viewModel)
            }
        }
    }
}

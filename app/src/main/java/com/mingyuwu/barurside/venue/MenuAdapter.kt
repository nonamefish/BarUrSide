package com.mingyuwu.barurside.venue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.databinding.ItemVenueMenuBinding
import com.mingyuwu.barurside.rating.RatingScoreAdapter

class MenuAdapter :
    ListAdapter<Drink, MenuAdapter.MenuViewHolder>(DiffCallback) {

    class MenuViewHolder(private var binding: ItemVenueMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): MenuViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemVenueMenuBinding.inflate(layoutInflater, parent, false)
                binding.lifecycleOwner = parent.context as LifecycleOwner

                return MenuViewHolder(binding)
            }
        }

        fun bind(drink: Drink, view: View) {
            binding.ratingScoreList.adapter = RatingScoreAdapter(13, 13)
            binding.drink = drink
            binding.cnstMenuDrink.setOnClickListener {
                view.findNavController()
                    .navigate(MainNavigationDirections.navigateToDrinkFragment(drink.id))
            }
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Drink] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Drink>() {
        override fun areItemsTheSame(oldItem: Drink, newItem: Drink): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Drink, newItem: Drink): Boolean {
            return oldItem.id == newItem.id
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuViewHolder {
        return MenuViewHolder.from(parent)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(getItem(position), holder.itemView)
    }
}

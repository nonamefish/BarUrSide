package com.mingyuwu.barurside.venue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.databinding.ItemVenueMenuBinding
import com.mingyuwu.barurside.rating.RatingScoreAdapter
import com.mingyuwu.barurside.rating.ScoreStatus

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
            Glide.with(binding.imgProduct.context)
                .load(drink.images?.getOrNull(0) ?: "")
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(binding.imgProduct)

            binding.txtProductName.text = drink.name

            binding.txtProductPrice.text = view.context.getString(R.string.drink_info_price, drink.price)

            val ratingScoreAdapter = RatingScoreAdapter(15, 15)
            binding.rvRatingScoreList.adapter = ratingScoreAdapter
            ratingScoreAdapter.submitList(MutableList(drink.avgRating.toInt()) { ScoreStatus.FULL })

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

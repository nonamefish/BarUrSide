package com.mingyuwu.barurside.rating

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.databinding.ItemUserRatingBinding

class UserRatingAdapter :
    ListAdapter<RatingInfo?, UserRatingAdapter.UserRatingViewHolder>(DiffCallback) {

    class UserRatingViewHolder(private var binding: ItemUserRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rating: RatingInfo?, view: View) {

            // setting navigate to venue/drink page
            binding.discoverObjectName.setOnClickListener {
                rating?.isVenue?.let {
                    if (it) {
                        view.findNavController()
                            .navigate(
                                MainNavigationDirections.navigateToVenueFragment(rating.objectId)
                            )
                    } else {
                        view.findNavController()
                            .navigate(
                                MainNavigationDirections.navigateToDrinkFragment(rating.objectId)
                            )
                    }
                }
            }

            binding.ratingScoreList.adapter = RatingScoreAdapter(15, 15)
            binding.rating = rating
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [RatingInfo] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<RatingInfo?>() {
        override fun areItemsTheSame(oldItem: RatingInfo, newItem: RatingInfo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: RatingInfo, newItem: RatingInfo): Boolean {
            return oldItem.id == newItem.id
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserRatingViewHolder {

        return UserRatingViewHolder(
            ItemUserRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: UserRatingViewHolder, position: Int) {
        holder.bind(getItem(position), holder.itemView)
    }
}

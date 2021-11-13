package com.mingyuwu.barurside.rating

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.databinding.ItemUserRatingBinding

class UserRatingAdapter() :
    ListAdapter<RatingInfo?, UserRatingAdapter.UserRatingViewHolder>(DiffCallback) {

    class UserRatingViewHolder(private var binding: ItemUserRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rating: RatingInfo?) {
            binding.ratingScoreList.adapter = RatingScoreAdapter(15, 15)
            binding.rating = rating
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
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
        val rating = getItem(position)
        holder.bind(rating)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

}
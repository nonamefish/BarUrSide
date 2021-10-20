package com.mingyuwu.barurside.rating

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.databinding.ItemUserRatingBinding

class RatingAdapter() :
    ListAdapter<Rating?, RatingAdapter.RatingViewHolder>(DiffCallback) {

    class RatingViewHolder(private var binding: ItemUserRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rating: Rating?) {
            binding.rating = rating
            binding.img=""
            binding.title="還沒串起來"

            binding.ratingScoreList.adapter = RatingScoreAdapter(15,15)
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Rating?>() {
        override fun areItemsTheSame(oldItem: Rating, newItem: Rating): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Rating, newItem: Rating): Boolean {
            return oldItem.id == newItem.id
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RatingViewHolder {

        return RatingViewHolder(
            ItemUserRatingBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val rating = getItem(position)
        holder.bind(rating)
    }

    override fun getItemCount(): Int {
        Log.d(com.mingyuwu.barurside.collect.TAG, "getItemCount : ${super.getItemCount()}")
        return super.getItemCount()
    }

}
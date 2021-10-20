package com.mingyuwu.barurside.rating

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.mockdata.UserData
import com.mingyuwu.barurside.databinding.ItemInfoRatingBinding

class InfoRatingAdapter() :
    ListAdapter<Rating?, InfoRatingAdapter.InfoRatingViewHolder>(DiffCallback) {

        class InfoRatingViewHolder(private var binding: ItemInfoRatingBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(rating: Rating?) {
                binding.ratingScoreList.adapter = RatingScoreAdapter(15,15)
                binding.ratingTagFrdList.adapter = UserImageAdapter()
                binding.rating = rating
                binding.user = UserData.user.user[0]
                binding.objectName = "大麻婆豆腐"
                binding.objectImg = "https://upload.wikimedia.org/wikipedia/commons/7/7f/%E9%BA%BB%E5%A9%86%E8%B1%86%E8%85%90.jpg"
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
        ): InfoRatingViewHolder {

            return InfoRatingViewHolder(
                ItemInfoRatingBinding.inflate(LayoutInflater.from(parent.context))
            )
        }

        // Replaces the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: InfoRatingViewHolder, position: Int) {
            val rating = getItem(position)
            holder.bind(rating)
        }

        override fun getItemCount(): Int {
            Log.d(com.mingyuwu.barurside.collect.TAG, "getItemCount : ${super.getItemCount()}")
            return super.getItemCount()
        }

    }
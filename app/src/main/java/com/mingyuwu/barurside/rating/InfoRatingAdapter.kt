package com.mingyuwu.barurside.rating

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.mockdata.UserData
import com.mingyuwu.barurside.databinding.ItemInfoRatingBinding
import com.mingyuwu.barurside.discoverdetail.DiscoverActivityAdapter

class InfoRatingAdapter() :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiscoverActivityAdapter) {

        class InfoRatingViewHolder(private var binding: ItemInfoRatingBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(rating: Rating?) {
                binding.ratingScoreList.adapter = RatingScoreAdapter(15,15)
                binding.ratingTagFrdList.adapter = UserImageAdapter(60)
                binding.rating = rating
                binding.user = UserData.user.user[0]
                binding.objectName = "大麻婆豆腐"
                binding.objectImg = "https://upload.wikimedia.org/wikipedia/commons/7/7f/%E9%BA%BB%E5%A9%86%E8%B1%86%E8%85%90.jpg"
            }
        }

        // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
        companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return (oldItem as Rating).id == (newItem as Rating).id
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
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is InfoRatingViewHolder -> {
                    Log.d("Ming", getItem(position).toString())
                    holder.bind((getItem(position) as Rating))
                }
            }
        }
    }
package com.mingyuwu.barurside.rating

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.data.mockdata.UserData
import com.mingyuwu.barurside.databinding.ItemEditRatingObjectBinding
import com.mingyuwu.barurside.databinding.ItemInfoRatingBinding
import com.mingyuwu.barurside.discoverdetail.DiscoverActivityAdapter
import com.mingyuwu.barurside.editrating.EditRatingAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InfoRatingAdapter() :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    class InfoRatingViewHolder(private var binding: ItemInfoRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): InfoRatingViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemInfoRatingBinding.inflate(layoutInflater, parent, false)
                binding.lifecycleOwner = parent.context as LifecycleOwner

                return InfoRatingViewHolder(binding)
            }
        }

        fun bind(rating: RatingInfo?, view: View) {
            Log.d("Ming","rating: $rating")

            // setting navigate to profile page
            binding.constraintUserInfo.setOnClickListener {
                if (rating != null) {
                    view.findNavController()
                        .navigate(MainNavigationDirections.navigateToProfileFragment(rating.userId))
                }
            }

            // set adapter
            binding.ratingScoreList.adapter = RatingScoreAdapter(15, 15)
            binding.ratingTagFrdList.adapter = UserImageAdapter(60)
            binding.rating = rating

            // set user Info
            binding.user = rating?.userInfo//UserData.user.user[0]
        }
    }


    // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (oldItem as RatingInfo).id == (newItem as RatingInfo).id
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InfoRatingViewHolder {
        return InfoRatingViewHolder.from(parent)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InfoRatingViewHolder -> {
                Log.d("Ming", getItem(position).toString())
                holder.bind((getItem(position) as RatingInfo), holder.itemView)
            }
        }
    }
}
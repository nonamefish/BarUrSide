package com.mingyuwu.barurside.rating

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.databinding.ItemInfoRatingBinding
import com.mingyuwu.barurside.util.Util.popUpMenuReport
import com.mingyuwu.barurside.util.Util.reportRating

class InfoRatingAdapter :
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

            // setting navigate to profile page
            binding.imgRtgUser.setOnClickListener {
                if (rating != null) {
                    view.findNavController()
                        .navigate(MainNavigationDirections.navigateToProfileFragment(rating.userId))
                }
            }

            // setting navigate to venue/drink page
            binding.constraintRtgInfo.setOnClickListener {
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

            // set adapter
            binding.ratingScoreList.adapter = RatingScoreAdapter(15, 15)
            binding.ratingTagFrdList.adapter = TagFriendAdapter()
            binding.rating = rating

            // set user Info
            binding.user = rating?.userInfo // UserData.user.user[0]

            // set report click listener
            binding.imgReport.setOnClickListener {
                popUpMenuReport(binding.imgReport, binding.root.context, rating?.id ?: "")
//                reportRating(binding.root.context, rating?.id ?: "")
            }
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [RatingInfo] has been updated.
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
        viewType: Int,
    ): InfoRatingViewHolder {
        return InfoRatingViewHolder.from(parent)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InfoRatingViewHolder -> {
                holder.bind((getItem(position) as RatingInfo), holder.itemView)
            }
        }
    }

}

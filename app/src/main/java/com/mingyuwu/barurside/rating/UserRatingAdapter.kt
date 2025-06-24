package com.mingyuwu.barurside.rating

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.databinding.ItemUserRatingBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserRatingAdapter :
    ListAdapter<RatingInfo?, UserRatingAdapter.UserRatingViewHolder>(DiffCallback) {

    class UserRatingViewHolder(private var binding: ItemUserRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rating: RatingInfo?, view: View) {
            // 1. 物件圖片
            val objImg = rating?.images?.getOrNull(0) ?: ""
            Glide.with(binding.imgDiscoverObject.context)
                .load(objImg)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(binding.imgDiscoverObject)
            // 2. 物件名稱
            binding.txtDiscoverObjectName.text = rating?.objectName

            // 3. 評論內容
            binding.txtDiscoverObjectCategory.text = rating?.comment

            // 4. 星星顯示
            val ratingScoreAdapter = RatingScoreAdapter(15, 15)
            binding.rvRatingScoreList.adapter = ratingScoreAdapter
            ratingScoreAdapter.submitList(MutableList((rating?.rating ?: 0).toInt()) { ScoreStatus.FULL })

            // 5. 日期
            rating?.postTimestamp?.let {
                binding.txtRatingDate.text =
                    SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(it.time))
            }

            // 點擊事件
            binding.txtDiscoverObjectName.setOnClickListener {
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

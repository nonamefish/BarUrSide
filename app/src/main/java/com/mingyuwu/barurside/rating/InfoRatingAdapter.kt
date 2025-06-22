package com.mingyuwu.barurside.rating

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
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.TagFriend
import com.mingyuwu.barurside.databinding.ItemInfoRatingBinding
import com.mingyuwu.barurside.util.Util.popUpMenuReport
import com.mingyuwu.barurside.util.Util.reportRating
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        fun bind(rating: RatingInfo?) {
            // user info
            binding.constraintUserInfo.visibility =
                if (rating?.userInfo == null) View.GONE else View.VISIBLE
            if (rating?.userInfo == null) return

            // user image
            rating.userInfo?.run {
                Glide.with(binding.imgRtgUser.context)
                    .load(image)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.imgRtgUser)

                binding.txtRtgUserName.text = name
                binding.txtUserPostCnt.text = shareCount?.toString()
                binding.txtUserPostimgCnt.text = shareImageCount?.toString()
            }

            // 報告按鈕
            binding.imgReport.setOnClickListener {
                popUpMenuReport(binding.imgReport, binding.root.context, rating.id)
            }

            Glide.with(binding.imgInfoRtg.context)
                .load(rating.images?.getOrNull(0))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(binding.imgInfoRtg)

            binding.txtInfoRtgObject.text = rating.objectName

            // 評分
            val starList = MutableList((rating.rating ?: 0).toInt()) { ScoreStatus.FULL }
            (binding.rvRatingScoreList.adapter as? RatingScoreAdapter)?.submitList(starList)

            // 評論內容
            binding.txtRtgContent.text = rating.comment

            // tag friend visibility
            val tagFriends = rating.tagFriends ?: emptyList<TagFriend>()
            val tagVisible = if (tagFriends.isEmpty()) View.GONE else View.VISIBLE
            binding.imgTagFrd.visibility = tagVisible
            binding.txtRatingTagFrds.visibility = tagVisible

            val tagFrdAdapter = TagFriendAdapter()
            tagFrdAdapter.submitList(tagFriends)
            binding.rvRatingTagFrdList.adapter = tagFrdAdapter

            // 日期
            rating.postTimestamp?.let {
                binding.txtRatingPostDate.text =
                    SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(it.time))
            }

            // 點擊事件
            binding.imgRtgUser.setOnClickListener {
                binding.root.findNavController()
                    .navigate(MainNavigationDirections.navigateToProfileFragment(rating.userId))
            }
            binding.constraintRtgInfo.setOnClickListener {
                rating.isVenue?.let {
                    if (it) {
                        binding.root.findNavController()
                            .navigate(MainNavigationDirections.navigateToVenueFragment(rating.objectId))
                    } else {
                        binding.root.findNavController()
                            .navigate(MainNavigationDirections.navigateToDrinkFragment(rating.objectId))
                    }
                }
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
                holder.bind((getItem(position) as RatingInfo))
            }
        }
    }

}

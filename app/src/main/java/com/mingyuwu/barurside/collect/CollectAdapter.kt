package com.mingyuwu.barurside.collect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.databinding.ItemCollectBinding
import com.mingyuwu.barurside.rating.RatingScoreAdapter
import kotlin.math.roundToInt

class CollectAdapter(val viewModel: CollectPageViewModel, val onClickListener: OnClickListener) :
    ListAdapter<Any, CollectAdapter.CollectViewHolder>(DiffCallback) {

    class CollectViewHolder(private var binding: ItemCollectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): CollectViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCollectBinding.inflate(layoutInflater, parent, false)
                binding.lifecycleOwner = parent.context as LifecycleOwner

                return CollectViewHolder(binding)
            }
        }

        fun bind(collect: Any, viewModel: CollectPageViewModel) {
            binding.viewModel = viewModel

            when (collect) {
                is Venue -> {
                    binding.ratingScoreList.adapter = RatingScoreAdapter(15, 15)
                    binding.venue = collect
                    binding.isVenue = true
                    binding.position = adapterPosition
                    viewModel.location.value?.let {
                        binding.distance =
                            SphericalUtil.computeDistanceBetween(
                                it,
                                LatLng(collect.latitude, collect.longitude)
                            ).roundToInt()
                    }
                    binding.rtgInfo = when (collect.rtgCount) {
                        0L -> {
                            "無評論"
                        }
                        else -> {
                            BarUrSideApplication.instance.getString(
                                R.string.venue_rating_info_view,
                                collect.avgRating,
                                collect.rtgCount
                            )
                        }
                    }
                }
                is Drink -> {
                    binding.ratingScoreList.adapter = RatingScoreAdapter(15, 15)
                    binding.drink = collect
                    binding.isVenue = false
                    binding.position = adapterPosition
                    binding.rtgInfo = when (collect.rtgCount) {
                        0L -> {
                            "無評論"
                        }
                        else -> {
                            BarUrSideApplication.instance.getString(
                                R.string.venue_rating_info_view,
                                collect.avgRating,
                                collect.rtgCount
                            )
                        }
                    }
                }
            }
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Collect] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when (oldItem) {
                is Venue -> oldItem.id == (newItem as Venue).id
                is Drink -> oldItem.id == (newItem as Drink).id
                else -> false
            }
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollectViewHolder {

        return CollectViewHolder.from(parent)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CollectViewHolder, position: Int) {
        val product = getItem(position)

        when (product) {
            is Venue -> {
                holder.itemView.setOnClickListener {
                    onClickListener.onClick(product.id)
                }
            }
            is Drink -> {
                holder.itemView.setOnClickListener {
                    onClickListener.onClick(product.id)
                }
            }
        }

        holder.bind(product, viewModel)
    }

    // Custom listener that handles clicks on RecyclerView items
    class OnClickListener(val clickListener: (id: String) -> Unit) {
        fun onClick(id: String) = clickListener(id)
    }
}

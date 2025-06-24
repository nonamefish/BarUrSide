package com.mingyuwu.barurside.collect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

    class CollectViewHolder(private val binding: ItemCollectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): CollectViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCollectBinding.inflate(layoutInflater, parent, false)
                return CollectViewHolder(binding)
            }
        }

        fun bind(collect: Any, viewModel: CollectPageViewModel) {
            when (collect) {
                is Venue -> {
                    binding.rvRatingScore.adapter = RatingScoreAdapter(15, 15)
                    updateImage(binding.imgCatalogProduct, collect.images?.first())
                    binding.txtCatalogProductName.text = collect.name
                    binding.txtCatalogProductDistance.visibility = View.VISIBLE
                    binding.txtCatalogProductDistance.text =
                        when (val distance = viewModel.location.value?.let {
                            SphericalUtil.computeDistanceBetween(
                                it,
                                LatLng(collect.latitude, collect.longitude)
                            ).roundToInt()
                        }) {
                            null -> ""
                            else -> "距 $distance 公尺"
                        }
                    binding.txtVenueRtgAvgScore.text = when (collect.rtgCount) {
                        0L -> "無評論"
                        else -> BarUrSideApplication.instance.getString(
                            R.string.venue_rating_info_view,
                            collect.avgRating,
                            collect.rtgCount
                        )
                    }
                    updateCollectButton(adapterPosition, viewModel)
                    setupCollectButton(collect.id, adapterPosition, true, viewModel)
                }

                is Drink -> {
                    binding.rvRatingScore.adapter = RatingScoreAdapter(15, 15)
                    updateImage(binding.imgCatalogProduct, collect.images?.first())
                    binding.txtCatalogProductName.text = collect.name
                    binding.txtCatalogProductDistance.visibility = View.GONE
                    binding.txtVenueRtgAvgScore.text = when (collect.rtgCount) {
                        0L -> "無評論"
                        else -> BarUrSideApplication.instance.getString(
                            R.string.venue_rating_info_view,
                            collect.avgRating,
                            collect.rtgCount
                        )
                    }
                    updateCollectButton(adapterPosition, viewModel)
                    setupCollectButton(collect.id, adapterPosition, false, viewModel)
                }
            }
            binding.imgCatalogProduct.apply {
                Glide.with(context)
                    .load(collect)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(this)
            }
        }

        private fun updateImage(view: ImageView, imgUrl: String?) {
            view.apply {
                Glide.with(context)
                    .load(imgUrl ?: "")
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(this)
            }
        }

        private fun updateCollectButton(position: Int, viewModel: CollectPageViewModel) {
            val isCollected = viewModel.isCollect.value?.get(position) == true
            binding.imgCollectFilled.apply {
                backgroundTintList = ContextCompat.getColorStateList(
                    context,
                    if (isCollected) R.color.red_e02401 else R.color.gray_888888
                )
                alpha = if (isCollected) 1.0f else 0.5f
            }
        }

        private fun setupCollectButton(
            id: String,
            position: Int,
            isVenue: Boolean,
            viewModel: CollectPageViewModel
        ) {
            binding.imgCollectFilled.setOnClickListener {
                viewModel.setCollect(id, position, isVenue)
                updateCollectButton(position, viewModel)
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

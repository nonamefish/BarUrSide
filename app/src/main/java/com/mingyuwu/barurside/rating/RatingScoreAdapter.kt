package com.mingyuwu.barurside.rating

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.databinding.ItemRatingScoreBinding

class RatingScoreAdapter(val width: Int, val height: Int) :
    ListAdapter<ScoreStatus, RatingScoreAdapter.RatingScoreViewHolder>(DiffCallback) {

    class RatingScoreViewHolder(private var binding: ItemRatingScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(starStatus: ScoreStatus) {
            binding.starStatus = starStatus
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of ScoreStatus has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<ScoreStatus>() {
        override fun areItemsTheSame(oldItem: ScoreStatus, newItem: ScoreStatus): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ScoreStatus, newItem: ScoreStatus): Boolean {
            return oldItem == newItem
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RatingScoreViewHolder {
        return RatingScoreViewHolder(
            ItemRatingScoreBinding
                .inflate(LayoutInflater.from(parent.context))
        )
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RatingScoreViewHolder, position: Int) {
        val starStatus = getItem(position)
        holder.bind(starStatus)
    }
}

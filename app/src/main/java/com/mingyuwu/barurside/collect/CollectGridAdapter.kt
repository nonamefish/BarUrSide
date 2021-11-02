package com.mingyuwu.barurside.collect

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.data.Collect
import com.mingyuwu.barurside.databinding.ItemCollectGridBinding
import com.mingyuwu.barurside.rating.RatingScoreAdapter

const val TAG = "CollectGridAdapter"

class CollectGridAdapter(val onClickListener: OnClickListener) :
    ListAdapter<Collect, CollectGridAdapter.CollectGridViewHolder>(DiffCallback) {

    class CollectGridViewHolder(private var binding: ItemCollectGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(collect: Collect) {
            binding.ratingScoreList.adapter = RatingScoreAdapter(15,15)
            binding.img = ""
            binding.title = "還沒串起來"
            binding.score = 2.5
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Collect>() {
        override fun areItemsTheSame(oldItem: Collect, newItem: Collect): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Collect, newItem: Collect): Boolean {
            return oldItem.id == newItem.id
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollectGridViewHolder {

        return CollectGridViewHolder(
            ItemCollectGridBinding
                .inflate(LayoutInflater.from(parent.context))
        )
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CollectGridViewHolder, position: Int) {
        val product = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(product)
        }
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount : ${super.getItemCount()}")
        return super.getItemCount()
    }

    // Custom listener that handles clicks on RecyclerView items
    class OnClickListener(val clickListener: (collect: Collect) -> Unit) {
        fun onClick(product: Collect) = clickListener(product)
    }
}
package com.mingyuwu.barurside.rating

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.collect.TAG
import com.mingyuwu.barurside.databinding.ItemRatingBitmapBinding
import com.mingyuwu.barurside.databinding.ItemRatingImageBinding

class BitmapAdapter (val width: Int, val height: Int) :
    ListAdapter<Bitmap, BitmapAdapter.ImageViewHolder>(DiffCallback) {

    class ImageViewHolder(private var binding: ItemRatingBitmapBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(img: Bitmap,width: Int,height: Int) {
            binding.img=img
            binding.width=width
            binding.height=height
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Bitmap>() {
        override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return oldItem == newItem
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewHolder {

        return ImageViewHolder(
            ItemRatingBitmapBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val img = getItem(position)
        holder.bind(img,width,height)
    }

    override fun getItemCount(): Int {
        Log.d(TAG,"getItemCount : ${super.getItemCount()}")
        return super.getItemCount()
    }
}
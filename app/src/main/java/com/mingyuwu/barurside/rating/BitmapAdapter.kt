package com.mingyuwu.barurside.rating

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.databinding.ItemRatingBitmapBinding
import com.mingyuwu.barurside.editrating.EditRatingViewModel

class BitmapAdapter(
    val width: Int,
    val height: Int,
    private val rtgOrder: Int,
    val viewModel: ViewModel
) :
    ListAdapter<Bitmap, BitmapAdapter.ImageViewHolder>(DiffCallback) {

    class ImageViewHolder(private var binding: ItemRatingBitmapBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            img: Bitmap,
            width: Int,
            height: Int,
            rtgOrder: Int,
            viewModel: ViewModel
        ) {
            binding.img = img
            binding.width = width
            binding.height = height

            when (viewModel) {
                is EditRatingViewModel -> {
                    binding.btnCancel.setOnClickListener {
                        viewModel.removeUploadImg(rtgOrder, adapterPosition)
                    }
                }
            }
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Bitmap] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Bitmap>() {
        override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return true
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
        holder.bind(img, width, height, rtgOrder, viewModel)
    }
}

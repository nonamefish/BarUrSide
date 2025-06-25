package com.mingyuwu.barurside.rating

import android.content.Context
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

        fun dpToPx(dp: Int, context: Context): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }

        fun bind(
            img: Bitmap,
            widthDp: Int,
            heightDp: Int,
            rtgOrder: Int,
            viewModel: ViewModel
        ) {
            binding.imgRtgUser.setImageBitmap(img)

            binding.imgRtgUser.layoutParams?.apply {
                this.width = dpToPx(widthDp, binding.imgRtgUser.context)
                this.height = dpToPx(heightDp, binding.imgRtgUser.context)
            }

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

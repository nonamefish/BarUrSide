package com.mingyuwu.barurside.editrating

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.databinding.ItemEditRatingObjectBinding
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.UserImageAdapter

class EditRatingAdapter(val isVenue: Boolean, private val viewModel: EditRatingViewModel) :
    ListAdapter<String, EditRatingAdapter.EditRatingViewHolder>(DiffCallback) {

    class EditRatingViewHolder(private var binding: ItemEditRatingObjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): EditRatingViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemEditRatingObjectBinding.inflate(layoutInflater, parent, false)
                binding.lifecycleOwner = parent.context as LifecycleOwner

                return EditRatingViewHolder(binding)
            }
        }

        fun bind(viewModel: EditRatingViewModel, isVenue: Boolean, position: Int) {

            binding.viewModel = viewModel
            binding.rtgOrder = position

            // set recyclerView
            val imgAdapter = ImageAdapter(15, 15)
            val tagFrdAdapter = UserImageAdapter()
            binding.ratingAddImgList.adapter = imgAdapter
            binding.ratingTagFrdsList.adapter = tagFrdAdapter

            binding.btnAddDrinkRtg.setOnClickListener {
                viewModel.addNewRating()
            }

            binding.btnAddImage.setOnClickListener {

                viewModel.addUploadImg(0,null)

            }

            binding.btnTagFrd.setOnClickListener {
                viewModel.addNewRating()
            }

            if (isVenue) {
                binding.btnAddDrinkRtg.visibility = View.VISIBLE
            } else {
                binding.btnAddDrinkRtg.visibility = View.GONE
            }

        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Product] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditRatingViewHolder {

        return EditRatingViewHolder.from(parent)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: EditRatingViewHolder, position: Int) {
        val id = getItem(position)
        holder.bind(viewModel, isVenue, position)
    }

    override fun getItemCount(): Int {
        Log.d("EditRatingAdapter", "getItemCount : ${super.getItemCount()}")
        return super.getItemCount()
    }

}
package com.mingyuwu.barurside.editrating

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.databinding.ItemEditRatingObjectBinding
import com.mingyuwu.barurside.rating.BitmapAdapter
import com.mingyuwu.barurside.rating.UserImageAdapter


class EditRatingAdapter( private val viewModel: EditRatingViewModel ) :
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

        fun bind(viewModel: EditRatingViewModel, position: Int) {

            binding.viewModel = viewModel
            binding.rtgOrder = position

            // set recyclerView adapter
            val imgAdapter = BitmapAdapter(60, 70)
            val tagFrdAdapter = UserImageAdapter(60)
            binding.ratingAddImgList.adapter = imgAdapter
            binding.ratingTagFrdsList.adapter = tagFrdAdapter


            // upload photo : set button click listener
            binding.btnAddImage.setOnClickListener {
                viewModel.isUploadImgBtn.value = true
                viewModel.clickPosition.value = position
            }

            // tag friend : set button click listener
            binding.btnTagFrd.setOnClickListener {
                viewModel.addTagFrd(position, "")
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

        holder.bind(viewModel, position)
    }

}


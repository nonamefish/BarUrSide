package com.mingyuwu.barurside.editrating

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.ItemEditRatingObjectBinding
import com.mingyuwu.barurside.rating.BitmapAdapter
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.UserImageAdapter


class EditRatingAdapter(private val viewModel: EditRatingViewModel) :
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

            // set button btnAddDrinkRtg visibility
            if (position==0) {
                binding.btnAddDrinkRtg.visibility = View.VISIBLE
            } else {
                binding.btnAddDrinkRtg.visibility = View.GONE
            }

            // set recyclerView adapter
            val imgAdapter = BitmapAdapter(60, 70)
            val tagFrdAdapter = UserImageAdapter(60)
            binding.ratingAddImgList.adapter = imgAdapter
            binding.ratingTagFrdsList.adapter = tagFrdAdapter

            // set button click listener
            binding.btnAddDrinkRtg.setOnClickListener {
                viewModel.addNewRating()
            }

            binding.btnAddImage.setOnClickListener {
                val bitmap = BitmapFactory.decodeResource(
                    BarUrSideApplication.appContext!!.resources,
                    R.drawable.image_placeholder
                )
                viewModel.addUploadImg(position, bitmap)
            }

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

    override fun getItemCount(): Int {
        Log.d("EditRatingAdapter", "getItemCount : ${super.getItemCount()}")
        return super.getItemCount()
    }

}
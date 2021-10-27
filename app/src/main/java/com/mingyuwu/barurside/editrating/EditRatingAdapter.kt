package com.mingyuwu.barurside.editrating

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
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

        fun bind(viewModel: EditRatingViewModel, rtgOrder: Int) {

            binding.viewModel = viewModel
            binding.rtgOrder = rtgOrder

            // set recyclerView adapter
            val imgAdapter = BitmapAdapter(60, 70)
            val tagFrdAdapter = UserImageAdapter(60)
            binding.ratingAddImgList.adapter = imgAdapter
            binding.ratingTagFrdsList.adapter = tagFrdAdapter


            // upload photo : set button click listener
            binding.btnAddImage.setOnClickListener {
                viewModel.isUploadImgBtn.value = true
                viewModel.clickPosition.value = rtgOrder
            }


            // tag friend : set adapter and item click listener
            viewModel.frdList.value?.let{
                val friendList = viewModel.frdList.value?.map { it.name}
                val adapter = ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_dropdown_item, friendList!!)
                binding.btnTagFrd.setAdapter(adapter)

                binding.btnTagFrd.setOnItemClickListener { _, _, position, _ ->
                    binding.btnTagFrd.setText("")
                    viewModel.addTagFrd(rtgOrder, it[position].id)
                }
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


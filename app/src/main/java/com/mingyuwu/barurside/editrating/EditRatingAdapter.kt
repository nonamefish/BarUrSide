package com.mingyuwu.barurside.editrating

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.collect.TAG
import com.mingyuwu.barurside.databinding.ItemEditRatingObjectBinding

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

        fun bind(viewModel: EditRatingViewModel, isVenue: Boolean) {

            binding.viewModel = viewModel
            binding.rtgOrder = viewModel.rtgOrder.value!!
            viewModel.addNewRating()

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
        holder.bind(viewModel, isVenue)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount : ${super.getItemCount()}")
        return super.getItemCount()
    }

}
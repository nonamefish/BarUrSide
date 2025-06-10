package com.mingyuwu.barurside.editrating

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.TagFriend
import com.mingyuwu.barurside.databinding.ItemEditRatingObjectBinding
import com.mingyuwu.barurside.rating.BitmapAdapter
import com.mingyuwu.barurside.rating.TagFriendAdapter

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

        fun bind(viewModel: EditRatingViewModel, rtgOrder: Int) {

            binding.viewModel = viewModel
            binding.rtgOrder = rtgOrder

            // set recyclerView adapter
            val imgAdapter = BitmapAdapter(80, 100, rtgOrder, viewModel)
            val tagFrdAdapter = TagFriendAdapter()
            binding.ratingAddImgList.adapter = imgAdapter
            binding.ratingTagFrdsList.adapter = tagFrdAdapter

            // upload photo : set button click listener
            binding.btnAddImage.setOnClickListener {
                viewModel.isUploadImgBtn.value = true
                viewModel.clickPosition.value = rtgOrder
            }

            binding.ratingAddImg.setOnClickListener {
                viewModel.isUploadImgBtn.value = true
                viewModel.clickPosition.value = rtgOrder
            }

            // tag friend : set adapter and item click listener
            binding.lifecycleOwner?.let {
                viewModel.frdList.observe(it, {

                        val friendList = viewModel.frdList.value?.map { "${it.name} (${it.id})" }
                        val adapter = ArrayAdapter(
                            binding.root.context,
                            R.layout.spinner_friend_list,
                            friendList!!
                        )

                        binding.btnTagFrd.setAdapter(adapter)

                        binding.btnTagFrd.setOnItemClickListener { parent, _, position, _ ->
                            val selected = parent.getItemAtPosition(position)
                            val pos = friendList.indexOf(selected)
                            binding.btnTagFrd.setText("")
                            viewModel.addTagFrd(TagFriend(it[pos].id, it[pos].name))
                            adapter.remove("${it[pos].name} (${it[pos].id})")
                        }
                    })
            }
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of item has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return true
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
        holder.bind(viewModel, position)
    }
}

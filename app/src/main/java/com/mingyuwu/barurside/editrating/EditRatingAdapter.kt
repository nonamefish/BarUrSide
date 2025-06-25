package com.mingyuwu.barurside.editrating

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
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

        private val starViews by lazy {
            listOf(
                binding.imgEditStar1,
                binding.imgEditStar2,
                binding.imgEditStar3,
                binding.imgEditStar4,
                binding.imgEditStar5
            )
        }

        private fun setRatingScore(star: Int) {
            starViews.forEachIndexed { index, imageView ->
                imageView.setImageResource(
                    if (star >= index + 1) R.drawable.ic_baseline_star_rate_24
                    else R.drawable.ic_baseline_star_border_24
                )
            }
        }

        companion object {
            fun from(parent: ViewGroup): EditRatingViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemEditRatingObjectBinding.inflate(layoutInflater, parent, false)

                return EditRatingViewHolder(binding)
            }
        }

        fun bind(viewModel: EditRatingViewModel, rtgOrder: Int) {
            // 1. 標題
            binding.txtVenue.text = viewModel.objectName.value?.getOrNull(rtgOrder) ?: ""

            // 2. 評論內容（雙向）
            binding.editTxtScoreList.setText(viewModel.comment.value?.getOrNull(rtgOrder) ?: "")
            binding.editTxtScoreList.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    viewModel.comment.value?.set(rtgOrder, s?.toString() ?: "")
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // 3. 星星顯示
            setRatingScore(viewModel.star.value?.getOrNull(rtgOrder) ?: 0)
            starViews.forEachIndexed { index, imageView ->
                imageView.setOnClickListener {
                    viewModel.clickRatingStore(index + 1, rtgOrder)
                }
            }

            // 4. constraintTagFrds visibility
            binding.constraintTagFrds.visibility = if (rtgOrder == 0) View.VISIBLE else View.GONE
            binding.txtClose.visibility = if (rtgOrder != 0) View.VISIBLE else View.GONE
            binding.floatingActionButton.visibility = if (rtgOrder != 0) View.VISIBLE else View.GONE

            // 5. 上傳圖片
            binding.btnAddImage.setOnClickListener {
                viewModel.isUploadImgBtn.value = true
                viewModel.clickPosition.value = rtgOrder
            }
            binding.cardAddImg.setOnClickListener {
                viewModel.isUploadImgBtn.value = true
                viewModel.clickPosition.value = rtgOrder
            }

            // 6. rvAddImgList 設定圖片
            val imgAdapter = BitmapAdapter(80, 100, rtgOrder, viewModel)
            binding.rvAddImgList.adapter = imgAdapter
            viewModel.uploadImg.value?.getOrNull(rtgOrder)
                ?.takeIf { it.filterNotNull().isNotEmpty() }
                ?.let { imgAdapter.submitList(it) }

            // 7. btnTagFrd 綁定朋友名單
            viewModel.frdList.value?.let { frds ->
                val friendList = frds.map { "${it.name} (${it.id})" }
                val adapter = ArrayAdapter(
                    binding.root.context,
                    R.layout.spinner_friend_list,
                    friendList
                )
                binding.btnTagFrd.setAdapter(adapter)
                binding.btnTagFrd.setOnItemClickListener { parent, _, position, _ ->
                    val selected = parent.getItemAtPosition(position)
                    val pos = friendList.indexOf(selected)
                    binding.btnTagFrd.setText("")
                    viewModel.addTagFrd(TagFriend(frds[pos].id, frds[pos].name))
                    adapter.remove("${frds[pos].name} (${frds[pos].id})")
                }
            }

            // 8. rvTagFrdsList 設定 tagFrd
            val tagFrdAdapter = TagFriendAdapter()
            binding.rvTagFrdsList.adapter = tagFrdAdapter
            tagFrdAdapter.submitList(viewModel.tagFrd.value)

            // 9. 刪除/移除
            binding.floatingActionButton.setOnClickListener {
                viewModel.removeRating(rtgOrder)
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

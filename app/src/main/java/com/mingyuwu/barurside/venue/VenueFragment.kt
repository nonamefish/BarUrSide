package com.mingyuwu.barurside.venue

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.databinding.FragmentVenueBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.InfoRatingAdapter
import com.mingyuwu.barurside.rating.RatingScoreAdapter

class VenueFragment : Fragment() {

    private lateinit var binding: FragmentVenueBinding
    private val viewModel by viewModels<VenueViewModel> {
        getVmFactory(
            VenueFragmentArgs.fromBundle(requireArguments()).id
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentVenueBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val id = VenueFragmentArgs.fromBundle(requireArguments()).id

        // navigate to all rating fragment
        viewModel.navigateToAll.observe(
            viewLifecycleOwner, {
                it?.let {
                    findNavController().navigate(
                        MainNavigationDirections.navigateToAllRatingFragment(it.toTypedArray())
                    )

                    viewModel.onLeft()
                }
            }
        )

        // set recyclerView Adapter
        val infoRatingAdapter = InfoRatingAdapter()
        val menuAdapter = MenuAdapter()
        val imgAdapter = ImageAdapter(80, 100)
        val ratingScoreAdapter = RatingScoreAdapter(15, 15)
        binding.rvRtgList.adapter = infoRatingAdapter
        binding.rvMenuList.adapter = menuAdapter
        binding.rvImgList.adapter = imgAdapter
        binding.rvRtgScoreList.adapter = ratingScoreAdapter

        // 星星顯示
        viewModel.starsList.observe(viewLifecycleOwner) { stars ->
            ratingScoreAdapter.submitList(stars)
        }

        // 菜單
        viewModel.menuInfo.observe(viewLifecycleOwner) { menu ->
            menuAdapter.submitList(menu?.sortedByDescending { it.avgRating }?.take(10))
            // 控制 txtMenuAll 顯示/隱藏
            binding.txtMenuAll.visibility = if ((menu?.size ?: 0) > 0) View.VISIBLE else View.GONE
        }

        // 圖片
        viewModel.images.observe(viewLifecycleOwner) { imgs ->
            imgAdapter.submitList(imgs?.take(10))
            binding.txtImg.text = if ((imgs?.size ?: 0) == 0) "尚無相片" else "相片 >"
        }

        // 評論
        viewModel.rtgInfos.observe(viewLifecycleOwner) { ratings ->
            infoRatingAdapter.submitList(ratings?.sortedByDescending { it.postDate }?.take(3))
            viewModel.setImages(ratings ?: emptyList())
            // 控制 txtAllReview 顯示/隱藏
            binding.txtAllReview.visibility = if ((ratings?.size ?: 0) > 3) View.VISIBLE else View.GONE
            binding.txtLastRtg.text = if ((ratings?.size ?: 0) == 0) "尚無評論" else "最新評論"
        }

        // 主要圖片、標題、評分、地址、電話等
        viewModel.venueInfo.observe(viewLifecycleOwner) { venue ->
            // 圖片
            val url = venue?.images?.getOrNull(0)
            if (!url.isNullOrEmpty()) {
                Glide.with(binding.imgVenue.context)
                    .load(url)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.imgVenue)
            } else {
                binding.imgVenue.setBackgroundResource(R.drawable.image_placeholder)
            }
            // 標題
            binding.txtVenueTitle.text = venue?.name ?: ""
            // 評分
            binding.txtRtgAvgScore.text = if ((venue?.rtgCount ?: 0) == 0L) {
                "無評論"
            } else {
                getString(R.string.venue_rating_info_view, venue?.avgRating ?: 0.0, venue?.rtgCount ?: 0)
            }
            // 地址
            binding.txtAddress.text = venue?.address ?: ""
            // 電話
            binding.txtPhone.text = venue?.phone ?: ""
        }

        // 營業狀態
        viewModel.serviceStatus.observe(viewLifecycleOwner) { status ->
            binding.txtServiceFlag.text = status
        }

        // set rating clock click listener
        binding.cnstrtEditRating.setOnClickListener {
            viewModel.venueInfo.value?.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToRatingFragment(it)
                )
            }
        }

        // set venue phone on click listener
        binding.txtPhone.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data =
                getString(R.string.venue_phone, viewModel.venueInfo.value?.phone).toUri()
            startActivity(dialIntent)
        }

        // check data had downloaded
        viewModel.status.observe(
            viewLifecycleOwner, {
                if (it == LoadStatus.DONE) {
                    binding.animationLoading.visibility = View.GONE
                    binding.cnstVenue.visibility = View.VISIBLE
                }
            }
        )

        // set menu on click listener
        viewModel.navigateToMenu.observe(
            viewLifecycleOwner, {
                it?.let {
                    findNavController().navigate(
                        MainNavigationDirections.navigateToDiscoverDetailFragment(
                            Theme.VENUE_MENU, listOf(id).toTypedArray(), null
                        )
                    )
                    viewModel.onLeft()
                }
            }
        )

        // set view all image's on click listener
        binding.txtImg.setOnClickListener {
            viewModel.images.value?.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToDiscoverDetailFragment(
                        Theme.IMAGES, it.toTypedArray(), null
                    )
                )
            }
        }

        // 搬遷 onClick 事件
        binding.imgCollectFilledFilled.setOnClickListener {
            viewModel.setCollect()
        }
        binding.txtMenu.setOnClickListener {
            viewModel.navigateToMenu()
        }
        binding.txtMenuAll.setOnClickListener {
            viewModel.navigateToMenu()
        }
        binding.txtAllReview.setOnClickListener {
            viewModel.navigateToAllRating()
        }

        return binding.root
    }
}

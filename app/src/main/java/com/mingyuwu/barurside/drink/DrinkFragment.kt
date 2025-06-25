package com.mingyuwu.barurside.drink

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.InfoRatingAdapter
import com.mingyuwu.barurside.rating.RatingScoreAdapter
import com.mingyuwu.barurside.databinding.FragmentDrinkBinding
import com.mingyuwu.barurside.util.Style

class DrinkFragment : Fragment() {

    private var _binding: FragmentDrinkBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DrinkViewModel> {
        getVmFactory(
            DrinkFragmentArgs.fromBundle(requireArguments()).id
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrinkBinding.inflate(inflater, container, false)

        // Set up RecyclerViews
        binding.rvDrinkRtgList.adapter = InfoRatingAdapter()
        binding.rvDrinkImgList.adapter = ImageAdapter(80, 100)
        binding.rvDrinkRtgScoreList.adapter = RatingScoreAdapter(15, 15)

        // Observe ViewModel data
        viewModel.drinkInfo.observe(viewLifecycleOwner) { drink ->
            drink?.let {
                binding.txtDrinkTitle.text = it.name
                binding.txtDrinkPrice.text = 
                    BarUrSideApplication.instance.getString(R.string.drink_info_price, it.price)
                binding.txtDrinkDesc.text = it.description
                binding.txtDrinkRtgAvgScore.text = when (it.rtgCount) {
                    0L -> "無評論"
                    else -> BarUrSideApplication.instance.getString(
                        R.string.venue_rating_info_view,
                        it.avgRating,
                        it.rtgCount
                    )
                }

                Glide.with(this)
                    .load(it.images?.firstOrNull() ?: "")
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.imgDrink)

            }
        }

        viewModel.venueInfo.observe(viewLifecycleOwner) { venue ->
            venue?.let {
                binding.txtDrinkByVenue.text = it.name
                binding.txtDrinkByVenueAddress.text = it.address
                binding.txtDrinkByVenueStyle.text = Style.valueOf(it.style).chinese

                Glide.with(this)
                    .load(it.images?.firstOrNull() ?: "")
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.imgVenue)
            }
        }

        viewModel.isCollect.observe(viewLifecycleOwner) { isCollected ->
            binding.imgCollectFilledFilled.apply {
                alpha = if (isCollected == true) 1.0f else 0.5f
                setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                        requireContext().getColor(
                            if (isCollected == true) R.color.red_e02401 else R.color.gray_888888
                        )
                    )
                )
            }
        }

        // Handle images and ratings text visibility
        viewModel.images.observe(viewLifecycleOwner) { images ->
            binding.txtDrinkImg.text = 
                if (images.isNullOrEmpty()) "尚無相片" else "相片 >"
            binding.txtDrinkRtg.text = 
                if (images.isNullOrEmpty()) "尚無評論" else "最新評論"
            (binding.rvDrinkImgList.adapter as ImageAdapter).submitList(images)
        }

        viewModel.rtgInfo.observe(viewLifecycleOwner) { ratings ->
            binding.txtDrinkRtgMore.visibility = 
                if ((ratings?.size ?: 0) > 3) View.VISIBLE else View.GONE
            (binding.rvDrinkRtgList.adapter as InfoRatingAdapter).submitList(ratings)
        }

        // Set up click listeners
        binding.imgCollectFilledFilled.setOnClickListener {
            viewModel.setCollect()
        }

        binding.btnVenueInfo.setOnClickListener {
            viewModel.venueInfo.value?.id?.let { venueId ->
                findNavController().navigate(
                    MainNavigationDirections.navigateToVenueFragment(venueId)
                )
            }
        }

        binding.txtDrinkImg.setOnClickListener {
            viewModel.images.value?.let { images ->
                findNavController().navigate(
                    MainNavigationDirections.navigateToDiscoverDetailFragment(
                        Theme.IMAGES, images.toTypedArray(), null
                    )
                )
            }
        }

        // Navigate to all ratings
        viewModel.navigateToAll.observe(viewLifecycleOwner) { ratings ->
            ratings?.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToAllRatingFragment(it.toTypedArray())
                )
                viewModel.onLeft()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


package com.mingyuwu.barurside.drink

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentDrinkBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.InfoRatingAdapter
import com.mingyuwu.barurside.rating.RatingScoreAdapter

class DrinkFragment : Fragment() {

    private lateinit var binding: FragmentDrinkBinding
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

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_drink, container, false
        )
        binding.lifecycleOwner = this

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

        // set drink data
        viewModel.drinkInfo.observe(
            viewLifecycleOwner, { drinkList ->
                drinkList?.let { drink ->
                    viewModel.getVenueResult(drink.venueId)
                    viewModel.getRatingResult(drink.id, false)

                    // after link viewModel venueInfo to datasource livedata,  then set binding viewModel
                    binding.viewModel = viewModel

                    // set rating data
                    viewModel.rtgInfo.observe(
                        viewLifecycleOwner, { rtgList ->
                            rtgList?.let { ratings ->
                                viewModel.setImages(ratings)
                                binding.ratings =
                                    ratings.sortedByDescending { rating -> rating.postDate }.take(3)
                                binding.imgs = viewModel.images.value?.take(10)
                            }
                        }
                    )
                }
            }
        )

        // set recyclerView Adapter
        binding.drinkRtgList.adapter = InfoRatingAdapter()
        binding.drinkImgList.adapter = ImageAdapter(80, 100)
        binding.drinkRtgScoreList.adapter = RatingScoreAdapter(15, 15)

        // drink's venue info click listener
        binding.btnVenueInfo.setOnClickListener {
            viewModel.venueInfo.value?.id.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToVenueFragment(it!!) // set venue id
                )
            }
        }

        // set view all image's on click listener
        binding.txtDrinkImg.setOnClickListener {
            viewModel.images.value?.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToDiscoverDetailFragment(
                        Theme.IMAGES, it.toTypedArray(), null
                    )
                )
            }
        }

        return binding.root
    }
}

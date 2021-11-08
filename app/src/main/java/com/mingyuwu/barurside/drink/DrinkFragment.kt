package com.mingyuwu.barurside.drink

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentDrinkBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.InfoRatingAdapter
import com.mingyuwu.barurside.rating.RatingScoreAdapter

const val TAG = "Ming"

class DrinkFragment : Fragment() {

    private lateinit var binding: FragmentDrinkBinding
    private val viewModel by viewModels<DrinkViewModel> {
        getVmFactory(
            DrinkFragmentArgs.fromBundle(requireArguments()).id
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_drink, container, false
        )
        binding.lifecycleOwner = this

        // get id
        val id = DrinkFragmentArgs.fromBundle(requireArguments()).id

        // navigate to all rating fragment
        viewModel.navigateToAll.observe(viewLifecycleOwner, Observer {
            it?.let{
                findNavController().navigate(MainNavigationDirections.navigateToAllRatingFragment(it.toTypedArray()))
                viewModel.onLeft()
            }
        })

        // set drink data
        viewModel.drinkInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.getVenueResult(it.venueId)
                viewModel.getRatingResult(it.id, false)

                // after link viewModel venueInfo to datasource livedata,  then set binding viewModel
                binding.viewModel = viewModel

                // set rating data
                viewModel.rtgInfo.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        binding.ratings = it.sortedByDescending { it.postDate }.take(3)
                        binding.imgs = viewModel.setImgs(it)
                    }
                })
            }
        })

        // set recyclerView Adapter
        binding.drinkRtgList.adapter = InfoRatingAdapter()
        binding.drinkImgList.adapter = ImageAdapter(80,100)
        binding.drinkRtgScoreList.adapter = RatingScoreAdapter(15,15)

        // drink's venue info click listener
        binding.btnVenueInfo.setOnClickListener {
            viewModel.venueInfo.value?.id.let{
                findNavController().navigate(
                    MainNavigationDirections.navigateToVenueFragment(it!!) // set venue id
                )
            }
        }

        return binding.root
    }

}
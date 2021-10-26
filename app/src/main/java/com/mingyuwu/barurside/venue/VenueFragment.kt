package com.mingyuwu.barurside.venue

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
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.mockdata.DrinkData
import com.mingyuwu.barurside.data.mockdata.RatingData
import com.mingyuwu.barurside.data.mockdata.VenueData
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.databinding.FragmentVenueBinding
import com.mingyuwu.barurside.drink.DrinkFragmentArgs
import com.mingyuwu.barurside.editrating.EditRatingViewModel
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.InfoRatingAdapter
import com.mingyuwu.barurside.rating.RatingScoreAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


const val TAG = "VenueFragment"

class VenueFragment : Fragment() {

    private lateinit var binding: FragmentVenueBinding
    private val viewModel by viewModels<VenueViewModel> {
        getVmFactory(
            VenueFragmentArgs.fromBundle(requireArguments()).id
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_venue, container, false
        )
        binding.lifecycleOwner = this

        val id = VenueFragmentArgs.fromBundle(requireArguments()).id

        // set recyclerView Adapter
        binding.venueRtgList.adapter = InfoRatingAdapter()
        binding.venueImgList.adapter = ImageAdapter(80, 100)
        binding.venueRtgScoreList.adapter = RatingScoreAdapter(15, 15)

        // set rating clock click listener
        binding.cnstrtEditRating.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToRatingFragment(id)
            )
        }

        // set venue data mockData
        // val venue = VenueData.venue.venue
        viewModel.venueInfo.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "venue info: ${it}")
            it?.let {
                binding.venue = it
            }
        })

        // set rating data mockData
        // val venue = VenueData.venue.venue
        viewModel.rtgInfo.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "rating info: ${it}")
            it?.let {
                binding.ratings = it
            }
        })


        return binding.root
    }

}
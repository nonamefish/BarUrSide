package com.mingyuwu.barurside.venue

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.InfoRatingAdapter
import com.mingyuwu.barurside.rating.RatingScoreAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class VenueFragment : Fragment() {

    private lateinit var binding: FragmentVenueBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_venue, container, false
        )

        // get id
        val id = VenueFragmentArgs.fromBundle(requireArguments()).id

        // mockData
//        val venue = VenueData.venue.venue
        GlobalScope.launch {
            val venue =
                (requireContext().applicationContext as BarUrSideApplication).repository.getVenue("318HjHNdV8XMHJ6ubrKU")
            binding.venue = venue.value
            Log.d("Ming","venue.value : ${venue.value}")
        }

        val ratings = RatingData.rating.rating

        // assign mock data to view variable
        binding.ratings = ratings


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

        return binding.root
    }

}
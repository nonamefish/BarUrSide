package com.mingyuwu.barurside.drink

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.mockdata.DrinkData
import com.mingyuwu.barurside.data.mockdata.RatingData
import com.mingyuwu.barurside.data.mockdata.VenueData
import com.mingyuwu.barurside.databinding.FragmentDrinkBinding
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.InfoRatingAdapter
import com.mingyuwu.barurside.rating.RatingScoreAdapter

class DrinkFragment : Fragment() {

    private lateinit var binding: FragmentDrinkBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_drink, container, false
        )

        // mockData
        val drink = DrinkData.drink.drink
        val venue = VenueData.venue.venue
        val ratings = RatingData.rating.rating

        // assign mock data to view variable
        binding.drink = drink[0]
        binding.venue = venue[0]
        binding.ratings = ratings

        // set recyclerView Adapter
        binding.drinkRtgList.adapter = InfoRatingAdapter()
        binding.drinkImgList.adapter = ImageAdapter(15,15)
        binding.drinkRtgScoreList.adapter = RatingScoreAdapter(15,15)


        return binding.root
    }

}
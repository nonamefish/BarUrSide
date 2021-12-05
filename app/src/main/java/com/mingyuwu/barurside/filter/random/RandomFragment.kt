package com.mingyuwu.barurside.filter.random

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.databinding.FragmentRandomBinding

class RandomFragment : Fragment() {

    private lateinit var binding: FragmentRandomBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val venues = RandomFragmentArgs.fromBundle(requireArguments()).venue.toList()
        binding = FragmentRandomBinding.inflate(inflater, container, false)

        val selectedItem = getRandomVenue(venues)

        // set selected Item name
        binding.chooseObject.text = selectedItem.name

        // set button back click listener
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // set button to venue
        binding.btnToVenue.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToVenueFragment(selectedItem.id)
            )
        }

        return binding.root
    }

    private fun getRandomVenue(venueList: List<Venue>): Venue {
        return venueList[(venueList.indices).random()]
    }
}

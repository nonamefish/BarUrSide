package com.mingyuwu.barurside.filter.random

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.databinding.FragmentRandomBinding


class RandomFragment : Fragment() {

    private lateinit var binding: FragmentRandomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val venue = RandomFragmentArgs.fromBundle(requireArguments()).venue.toList()
        binding = FragmentRandomBinding.inflate(inflater, container, false)


        // set button back click listener
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // set button to venue
        binding.btnToVenue.setOnClickListener {
            findNavController().navigate(MainNavigationDirections.navigateToVenueFragment(venue[0].id))
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}
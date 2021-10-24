package com.mingyuwu.barurside.discover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.databinding.FragmentDiscoverBinding

class DiscoverFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentDiscoverBinding.inflate(layoutInflater)

        // set onclick listener
        binding.cnstrtAroundVenue.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.AROUND_VENUE,null
                )
            )
        }

        binding.cnstrtRecentActivity.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.RECENT_ACTIVITY,null
                )
            )
        }

        binding.cnstrtHotDrink.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.HOT_DRINK,null
                )
            )
        }

        binding.cnstrtHotVenue.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.HOT_VENUE,null
                )
            )
        }

        binding.cnstrtHighRateDrink.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.HIGH_RATE_DRINK,null
                )
            )
        }

        binding.cnstrtHighRateVenue.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.HIGH_RATE_VENUE,null
                )
            )
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}
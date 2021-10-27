package com.mingyuwu.barurside.venue

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.mingyuwu.barurside.databinding.FragmentVenueBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.InfoRatingAdapter
import com.mingyuwu.barurside.rating.RatingScoreAdapter


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
            viewModel.venueInfo.value?.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToRatingFragment(it)
                )
            }
        }

        // set venue data mockData
        viewModel.venueInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.venue = it
            }
        })

        // set rating data mockData
        viewModel.rtgInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.ratings = it.take(3)
                binding.imgs = viewModel.setImgs(it)
            }
        })

        // set venue phone on click listener
        binding.venuePhone.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + viewModel.venueInfo.value?.phone)
            startActivity(dialIntent)
        }

        // set menu on click listener
        binding.venueMenu.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.VENUE_MENU, id, null
                )
            )
        }

        return binding.root
    }

}
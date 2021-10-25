package com.mingyuwu.barurside.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.mockdata.RatingData
import com.mingyuwu.barurside.databinding.FragmentProfileBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.discoverdetail.DiscoverDetailFragmentArgs
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.UserRatingAdapter


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val id = ProfileFragmentArgs.fromBundle(requireArguments()).id


        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )

        binding.lifecycleOwner = this

        // data
        val rtg = RatingData.rating

        // test image adapter
        val imgAdapter = ImageAdapter(80,100)
        binding.venueImgList.adapter=imgAdapter
        imgAdapter.submitList(listOf("","",""))

        // test rating adapter
        val rtgVnAdapter = UserRatingAdapter()
        binding.userRtgVenueList.adapter=rtgVnAdapter
        rtgVnAdapter.submitList(rtg.rating)

        // test drink adapter
        val rtgDkAdapter = UserRatingAdapter()
        binding.userRtgDrinkList.adapter=rtgDkAdapter
        rtgDkAdapter.submitList(rtg.rating)

        // test friend adapter

        binding.myFriend.setOnClickListener {
            findNavController().navigate(MainNavigationDirections.navigateToDiscoverDetailFragment(Theme.USER_FRIEND,id,null))
        }

        // test activity adapter
        binding.myActivity.setOnClickListener {
            findNavController().navigate(MainNavigationDirections.navigateToDiscoverDetailFragment(Theme.USER_ACTIVITY,id,null))
        }

        return binding.root
    }

}
package com.mingyuwu.barurside.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.mockdata.RatingData
import com.mingyuwu.barurside.databinding.FragmentProfileBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.discoverdetail.DiscoverDetailFragmentArgs
import com.mingyuwu.barurside.drink.DrinkFragmentArgs
import com.mingyuwu.barurside.drink.DrinkViewModel
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.UserRatingAdapter


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel> {
        getVmFactory(
            ProfileFragmentArgs.fromBundle(requireArguments()).id ?: UserManager.user.value!!.id
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // user profile will get id from UserManager
        val id = ProfileFragmentArgs.fromBundle(requireArguments()).id ?: UserManager.user.value!!.id

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // test image adapter
        val imgAdapter = ImageAdapter(80, 100)
        binding.venueImgList.adapter = imgAdapter


        // set rating adapter of drink and venue
        val rtgVnAdapter = UserRatingAdapter()
        binding.userRtgVenueList.adapter = rtgVnAdapter

        val rtgDkAdapter = UserRatingAdapter()
        binding.userRtgDrinkList.adapter = rtgDkAdapter

        // data
        viewModel.rtgInfo.observe(viewLifecycleOwner, Observer { rtgInfo ->
            rtgInfo?.let { rtgs ->
                val sortRtgs = rtgs.sortedByDescending { it.postTimestamp }
                rtgVnAdapter.submitList(sortRtgs.filter { it.isVenue == true }
                    .take(3))
                rtgDkAdapter.submitList(sortRtgs.filter { it.isVenue == false }
                    .take(3))
                imgAdapter.submitList(viewModel.setImgs(sortRtgs))
            }
        })

        // test friend adapter
        binding.myFriend.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.USER_FRIEND,
                    viewModel.userInfo.value?.friends?.map { it.id }?.toTypedArray(),
                    null
                )
            )
        }

        // test activity adapter
        binding.myActivity.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.USER_ACTIVITY,
                    listOf(id!!).toTypedArray(),
                    null
                )
            )
        }

        return binding.root
    }

}
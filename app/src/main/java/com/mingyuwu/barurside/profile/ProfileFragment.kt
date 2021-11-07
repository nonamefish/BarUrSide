package com.mingyuwu.barurside.profile

import android.os.Bundle
import android.util.Log
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
            ProfileFragmentArgs.fromBundle(requireArguments()).id ?: (UserManager.user.value?.id
                ?: "")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // user profile will get id from UserManager
        val id =
            ProfileFragmentArgs.fromBundle(requireArguments()).id ?: (UserManager.user.value?.id
                ?: "")

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

                // filter data
                val rtgVenue = sortRtgs.filter { it.isVenue == true }.take(3)
                val rtgDrink = sortRtgs.filter { it.isVenue == false }.take(3)

                // recyclerView submit list
                rtgVnAdapter.submitList(rtgVenue)
                rtgDkAdapter.submitList(rtgDrink)
                imgAdapter.submitList(viewModel.setImgs(sortRtgs))

                // set binding variable
                binding.rtgVenueCnt = rtgVenue.size
                binding.rtgDrinkCnt = rtgDrink.size
            }
        })

        // notification: if user send or get add friend request, then can't click add button
        viewModel.notification.observe(viewLifecycleOwner, Observer {
            it?.let { notifications ->
                val list = notifications.filter { notification -> notification.reply == null }
                if (list.any { it.fromId == UserManager.user.value?.id }) {
                    binding.btnAddFrd.text = "好友邀請寄送中"
                    binding.btnAddFrd.isEnabled = false
                } else if (list.any { it.toId == UserManager.user.value?.id }) {
                    binding.btnAddFrd.text = "好友邀請確認中"
                    binding.btnAddFrd.isEnabled = false
                }
            }
        })

        // navigate to friend list page
        binding.myFriend.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.USER_FRIEND,
                    viewModel.userInfo.value?.friends?.map { it.id }?.toTypedArray(),
                    null
                )
            )
        }

        // navigate to activity page
        binding.myActivity.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.USER_ACTIVITY,
                    listOf(id!!).toTypedArray(),
                    null
                )
            )
        }

        // set add friend listener
        binding.btnAddFrd.setOnClickListener {
            if (viewModel.isFriend != true) {
                viewModel.addOnFriend()
            }
        }


        return binding.root
    }

}
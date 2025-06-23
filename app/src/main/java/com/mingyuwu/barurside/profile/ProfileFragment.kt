package com.mingyuwu.barurside.profile

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.databinding.FragmentProfileBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.UserRatingAdapter

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel> {
        getVmFactory(
            ProfileFragmentArgs.fromBundle(
                requireArguments()
            ).id ?: (UserManager.user.value?.id ?: "")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id =
            ProfileFragmentArgs.fromBundle(requireArguments()).id ?: (UserManager.user.value?.id
                ?: "")
        val toolbarTitle = requireActivity().findViewById<TextView>(R.id.text_toolbar_title)

        // image adapter
        val imgAdapter = ImageAdapter(80, 100)
        binding.rvProfileImgList.adapter = imgAdapter
        // rating adapter
        val rtgVnAdapter = UserRatingAdapter()
        binding.rvUserRtgVenueList.adapter = rtgVnAdapter
        val rtgDkAdapter = UserRatingAdapter()
        binding.rvUserRtgDrinkList.adapter = rtgDkAdapter

        // user info
        viewModel.userInfo.observe(viewLifecycleOwner) { user ->
            user?.let {
                toolbarTitle.text = it.name
                binding.txtUserName.text = it.id
                binding.txtUserFriendCnt.text =
                    getString(R.string.profile_frds_cnt, it.friends?.size)
                Glide.with(binding.imgUser.context)
                    .load(it.image)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.imgUser)
                binding.txtVenueRtgValue.text = it.shareCount.toString()
                binding.txtDrinkRtgValue.text = it.shareImageCount.toString()
            }
        }

        // ratings data
        viewModel.rtgInfos.observe(viewLifecycleOwner) { rtgInfo ->
            rtgInfo?.let { rtgs ->
                val sortRtgs = rtgs.sortedByDescending { it.postTimestamp }
                viewModel.setImages(rtgs)
                val rtgVenue = sortRtgs.filter { it.isVenue == true }
                val rtgDrink = sortRtgs.filter { it.isVenue == false }
                rtgVnAdapter.submitList(rtgVenue.take(3))
                rtgDkAdapter.submitList(rtgDrink.take(3))
                imgAdapter.submitList(viewModel.images.value?.take(10))
                binding.txtVenueRtgCnt.text = getString(R.string.profile_rtgs_cnt, rtgVenue.size)
                binding.txtDrinkRtgCnt.text = getString(R.string.profile_rtgs_cnt, rtgDrink.size)
                binding.txtProfileRating.text =
                    if (rtgVenue.size > 3) getString(R.string.venue_rating_amount_more) else getString(
                        R.string.venue_rating_amount
                    )
                binding.txtDrinkRating.text =
                    if (rtgDrink.size > 3) getString(R.string.drink_rating_amount_more) else getString(
                        R.string.drink_rating_amount
                    )
            }
        }

        // images
        viewModel.images.observe(viewLifecycleOwner) { images ->
            if (images.isNullOrEmpty()) {
                binding.txtProfileImg.text = getString(R.string.no_image)
                binding.txtProfileImg.isClickable = false
            } else {
                binding.txtProfileImg.text = getString(R.string.image_more)
                binding.txtProfileImg.isClickable = true
            }
        }

        // notification: if user send or get add friend request, then can't click add button
        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            notifications?.let {
                val listNotReply = it.filter { notification -> notification.reply == null }
                if (listNotReply.any { n -> n.toId == id }) {
                    binding.btnAddFriend.text = getString(R.string.friend_requesting)
                    binding.btnAddFriend.isEnabled = false
                } else if (listNotReply.any { n -> n.fromId == id }) {
                    binding.btnAddFriend.text = getString(R.string.friend_confirm)
                    binding.btnAddFriend.isEnabled = false
                } else {
                    binding.btnAddFriend.isEnabled = true
                }
            }
        }

        // friend status
        viewModel.isFriend.observe(viewLifecycleOwner) { isFriend ->
            binding.btnAddFriend.text = getString(
                if (isFriend) R.string.status_friend else R.string.add_friend
            )
            binding.btnAddFriend.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (isFriend) R.color.gray_888888 else R.color.primaryColor
            )
            binding.imgIsFriend.visibility = if (isFriend) View.VISIBLE else View.GONE
        }

        // myself status
        if (viewModel.isMyself) {
            binding.btnAddFriend.visibility = View.GONE
        } else {
            binding.btnAddFriend.visibility = View.VISIBLE
        }

        // loading
        viewModel.status.observe(viewLifecycleOwner) { status ->
            binding.animationLoading.isVisible = status != LoadStatus.DONE
            binding.constProfile.isVisible = status == LoadStatus.DONE
        }

        // navigate to all rating fragment
        viewModel.navigateToAll.observe(viewLifecycleOwner) { itList ->
            itList?.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToAllRatingFragment(it.toTypedArray())
                )
                viewModel.onLeft()
            }
        }

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
                    listOf(id).toTypedArray(),
                    null
                )
            )
        }
        // set add friend listener
        binding.btnAddFriend.setOnClickListener {
            if (viewModel.isFriend.value != true) {
                viewModel.addOnFriend()
            } else {
                showUnfriendConfirm()
            }
        }
        // set view all image's on click listener
        binding.txtProfileImg.setOnClickListener {
            viewModel.images.value?.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToDiscoverDetailFragment(
                        Theme.IMAGES, it.toTypedArray(), null
                    )
                )
            }
        }
        // check user friend status: when unfriend then change isFriend status
        UserManager.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                viewModel.isFriend.value = UserManager.user.value?.friends?.any { it.id == id }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showUnfriendConfirm() {
        // set alert dialog view
        val alertDialog = AlertDialog.Builder(binding.root.context)
        val mView =
            LayoutInflater.from(context).inflate(R.layout.dialog_rating_uncompleted, null, false)
        alertDialog.setView(mView)
        val dialog = alertDialog.create()

        // set dialog title and content
        val titleDialog = mView.findViewById<TextView>(R.id.dialog_title)
        titleDialog.text = getString(R.string.unfriend_notify_title, viewModel.userInfo.value?.name)
        val txtDialog = mView.findViewById<TextView>(R.id.dialog_content)
        txtDialog.text = getString(R.string.unfriend_notify_content)

        // set button click listener
        val btDialog = mView.findViewById<Button>(R.id.button_confirm)
        btDialog.setOnClickListener {
            viewModel.unfriendUser()
            dialog.dismiss()
        }

        dialog.show()

        // set border as transparent
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}

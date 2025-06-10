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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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

    private lateinit var binding: FragmentProfileBinding
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

        // user profile will get id from UserManager
        val id =
            ProfileFragmentArgs.fromBundle(
                requireArguments()
            ).id ?: (UserManager.user.value?.id ?: "")
        val toolbarTitle = requireActivity().findViewById<TextView>(R.id.text_toolbar_title)

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // test image adapter
        val imgAdapter = ImageAdapter(80, 100)
        binding.profileImgList.adapter = imgAdapter

        // set rating adapter of drink and venue
        val rtgVnAdapter = UserRatingAdapter()
        binding.userRtgVenueList.adapter = rtgVnAdapter

        val rtgDkAdapter = UserRatingAdapter()
        binding.userRtgDrinkList.adapter = rtgDkAdapter

        // set tool bar title by user display name
        viewModel.userInfo.observe(
            viewLifecycleOwner, {
                it?.let {
                    toolbarTitle.text = it.name
                }
            }
        )

        // observe ratings data
        viewModel.rtgInfos.observe(
            viewLifecycleOwner, { rtgInfo ->
                rtgInfo?.let { rtgs ->
                    val sortRtgs = rtgs.sortedByDescending { it.postTimestamp }
                    viewModel.setImages(rtgs)

                    // filter data
                    val rtgVenue = sortRtgs.filter { it.isVenue == true }
                    val rtgDrink = sortRtgs.filter { it.isVenue == false }

                    // recyclerView submit list
                    rtgVnAdapter.submitList(rtgVenue.take(3))
                    rtgDkAdapter.submitList(rtgDrink.take(3))
                    imgAdapter.submitList(viewModel.images.value?.take(10))

                    // set binding variable
                    binding.rtgVenueCnt = rtgVenue.size
                    binding.rtgDrinkCnt = rtgDrink.size
                }
            }
        )

        // notification: if user send or get add friend request, then can't click add button
        viewModel.notifications.observe(
            viewLifecycleOwner, { list ->
                list?.let { notifications ->
                    val listNotReply = notifications.filter { notification ->
                        notification.reply == null
                    }
                    if (listNotReply.any { it.toId == id }) {

                        binding.btnAddFrd.text = getString(R.string.friend_requesting)
                        binding.btnAddFrd.isEnabled = false
                    } else if (listNotReply.any { it.fromId == id }) {

                        binding.btnAddFrd.text = getString(R.string.friend_confirm)
                        binding.btnAddFrd.isEnabled = false
                    }
                }
            }
        )

        // navigate to all rating fragment
        viewModel.navigateToAll.observe(
            viewLifecycleOwner, {
                it?.let {
                    findNavController().navigate(
                        MainNavigationDirections.navigateToAllRatingFragment(it.toTypedArray())
                    )
                    viewModel.onLeft()
                }
            }
        )

        // check loading done and close loading animation
        viewModel.status.observe(
            viewLifecycleOwner, {
                if (it == LoadStatus.DONE) {
                    binding.animationLoading.visibility = View.GONE
                    binding.constProfile.visibility = View.VISIBLE
                }
            }
        )

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
        binding.btnAddFrd.setOnClickListener {
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
        UserManager.user.observe(
            viewLifecycleOwner, { user ->
                user?.let {
                    viewModel.isFriend.value = UserManager.user.value?.friends?.any { it.id == id }
                }
            }
        )

        return binding.root
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

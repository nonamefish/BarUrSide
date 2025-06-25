package com.mingyuwu.barurside.activity.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mingyuwu.barurside.Constants.DEEPLINK_ACTIVITY
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.DialogActivityDetailBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.login.UserManager
import androidx.core.graphics.drawable.toDrawable

class ActivityDetailDialog : DialogFragment() {

    private lateinit var binding: DialogActivityDetailBinding
    private val viewModel by viewModels<ActivityDetailViewModel> {
        getVmFactory(
            ActivityDetailDialogArgs.fromBundle(requireArguments()).activity,
            ActivityDetailDialogArgs.fromBundle(requireArguments()).activityId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogActivityDetailBinding.inflate(inflater, container, false)
        val theme = ActivityDetailDialogArgs.fromBundle(requireArguments()).theme

        // Set up click listeners
        binding.btnActivityDetailJoin.setOnClickListener {
            if (viewModel.hasBook.value == true) {
                viewModel.modifyActivity()
            } else if (viewModel.hasBook.value == false) {
                viewModel.bookActivity()
            }
        }

        binding.clSponsorInfo.setOnClickListener {
            viewModel.sponsor.value?.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToProfileFragment(it.id)
                )
            }
        }

        binding.imgShare.setOnClickListener {
            val message = getString(
                R.string.activity_share_message,
                viewModel.dtActivity.value?.name,
                "$DEEPLINK_ACTIVITY=${viewModel.dtActivity.value?.id}"
            )

            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, message)

            startActivity(Intent.createChooser(share, getString(R.string.app_name)))
        }

        // Set transparent background
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }

        // Observe navigation events
        viewModel.navigateToDetail.observe(viewLifecycleOwner) {
            it?.let {
                val id = findNavController().previousBackStackEntry?.destination?.label

                if (id == "ActivityFragment") {
                    findNavController().popBackStack()
                } else {
                    findNavController().navigate(
                        MainNavigationDirections.navigateToDiscoverDetailFragment(
                            theme,
                            listOf(UserManager.user.value?.id ?: "").toTypedArray(),
                            null
                        )
                    )
                }
                viewModel.onLeft()
            }
        }

        // Observe activity data changes
        viewModel.dtActivity.observe(viewLifecycleOwner) { activity ->
            activity?.let {
                binding.txtActivityDetailName.text = it.name
                binding.txtActivityDetailTime.text = viewModel.formatDate(it.startTime)
                binding.txtActivityDetailEndtime.text = viewModel.formatDate(it.endTime)
                binding.txtActivityDetailAddress.text = it.address
                binding.txtActivityDetailLimit.text = getString(
                    R.string.activity_detail_limit,
                    it.bookers?.size ?: 0,
                    it.peopleLimit ?: 0
                )
                binding.txtActivityDetailMain.text = it.mainDrinking
                binding.animationLoading.visibility = View.GONE
                binding.clActivityDetail.visibility = View.VISIBLE

                // Update join button state based on isFull condition
                val isFull = it.bookers?.size?.toLong() == it.peopleLimit
                binding.btnActivityDetailJoin.apply {
                    isEnabled = !isFull
                    text = when {
                        isFull -> getString(R.string.activity_full)
                        viewModel.hasBook.value == true -> getString(R.string.activity_quit)
                        else -> getString(R.string.activity_join)
                    }
                }
            }
        }

        // Observe sponsor data changes
        viewModel.sponsor.observe(viewLifecycleOwner) { sponsor ->
            sponsor?.let {
                binding.txtActivityDetailSponsorName.text = it.name
                Glide.with(this)
                    .load(it.image)
                    .circleCrop()
                    .into(binding.imgRtgUser)
            }
        }

        return binding.root
    }
}

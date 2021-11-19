package com.mingyuwu.barurside.activity.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.DialogActivityDetailBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.login.UserManager
import android.content.Intent
import android.net.Uri


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
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_activity_detail, container, false
        )
        val theme = ActivityDetailDialogArgs.fromBundle(requireArguments()).theme
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // book button on click listener
        binding.btnBookActivity.setOnClickListener {
            if (viewModel.isBook.value == true) {
                viewModel.modifyActivity()
            } else if (viewModel.isBook.value == false) {
                viewModel.bookActivity()
            }
        }

        // navigate to sponsor profile page
        binding.constraintSponsorInfo.setOnClickListener {
            viewModel.sponsor.value?.let {
                findNavController().navigate(MainNavigationDirections.navigateToProfileFragment(it.id))
            }
        }

        // Set transparent background and no title
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        }

        // navigate to activity
        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            Log.d("Ming", "navigateToDetail : ${viewModel.navigateToDetail.value}")
            it?.let {
                val id = findNavController().previousBackStackEntry?.destination?.label

                if (id == "ActivityFragment") {
                    findNavController().popBackStack()
                } else {
                    findNavController().popBackStack()
//                    findNavController().navigate(
//                        MainNavigationDirections.navigateToDiscoverDetailFragment(
//                            theme!!,
//                            listOf(UserManager.user.value?.id ?: "").toTypedArray(),
//                            null
//                        )
//                    )
                }
                viewModel.onLeft()
            }
        })

        // refresh viewModel setting
        viewModel.dtActivity.observe(viewLifecycleOwner, Observer {
            binding.viewModel = viewModel
        })

        // sharing button
        binding.imgShare.setOnClickListener {
            val message =
                "與您分享品酒活動：${viewModel.dtActivity.value?.name} \n " +
                        "https://www.barurside.com/activity?id=${viewModel.dtActivity.value?.id}"
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, message)

            startActivity(Intent.createChooser(share, "Title of the dialog the system will open"))
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        this.dismiss()
    }

}
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
import com.mingyuwu.barurside.addactivity.AddActivityViewModel
import com.mingyuwu.barurside.data.mockdata.UserData
import com.mingyuwu.barurside.databinding.ActivityMainBinding
import com.mingyuwu.barurside.databinding.DialogActivityDetailBinding
import com.mingyuwu.barurside.databinding.FragmentDrinkBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.login.UserManager

class ActivityDetailDialog : DialogFragment() {

    private lateinit var binding: DialogActivityDetailBinding
    private val viewModel by viewModels<ActivityDetailViewModel> {
        getVmFactory(
            ActivityDetailDialogArgs.fromBundle(requireArguments()).activity
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
            if (viewModel.isBook) {
                viewModel.modifyActivity()
            } else {
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
            it?.let {
                val id = findNavController().previousBackStackEntry?.destination?.label

                Log.d("Ming", "previous destination: $id")


                if (id == "ActivityFragment") {
                    findNavController().popBackStack()
                } else {
                    findNavController().navigate(
                        MainNavigationDirections.navigateToDiscoverDetailFragment(
                            theme!!,
                            listOf(UserManager.user.value?.id ?: "").toTypedArray(),
                            null
                        )
                    )
                }

            }
        })

        return binding.root
    }
}
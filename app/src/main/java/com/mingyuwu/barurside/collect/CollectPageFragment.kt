package com.mingyuwu.barurside.collect

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.databinding.FragmentCollectPageBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.ext.isPermissionGranted
import com.mingyuwu.barurside.ext.requestPermission
import com.mingyuwu.barurside.util.AppPermission
import com.mingyuwu.barurside.util.Location
import com.mingyuwu.barurside.util.Logger
import com.mingyuwu.barurside.util.Util

class CollectPageFragment : Fragment() {

    val isVenue = Util.getString(R.string.collect_tab_is_venue)
    private lateinit var binding: FragmentCollectPageBinding
    private lateinit var mContext: Context
    private val viewModel by viewModels<CollectPageViewModel> {
        getVmFactory(
            this.requireArguments().getBoolean(isVenue)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentCollectPageBinding.inflate(inflater, container, false)
        val isVenue = this.requireArguments().getBoolean(isVenue)

        // set variable for get location
        mContext = binding.root.context
        getDeviceLocation()

        // set recyclerView adapter and get collect id list
        val adapter = CollectAdapter(
            viewModel,
            CollectAdapter.OnClickListener {
                viewModel.setNavigateToObject(it)
            }
        )

        binding.collectList.adapter = adapter

        viewModel.collectInfo.observe(
            viewLifecycleOwner, {
                if (!it.isNullOrEmpty()) {
                    viewModel.getObjectInfo(isVenue, it)
                } else {
                    binding.animationEmpty.visibility = View.VISIBLE
                    binding.animationLoading.visibility = View.GONE
                }
            }
        )

        // get collect object information
        viewModel.objectInfo.observe(
            viewLifecycleOwner, {
                if (it.isEmpty()) {
                    binding.animationEmpty.visibility = View.VISIBLE
                } else {
                    binding.animationLoading.visibility = View.GONE
                    adapter.submitList(it)
                }
            }
        )

        // set navigation to object info page
        viewModel.navigateToObject.observe(
            viewLifecycleOwner, {
                it?.let {
                    when (isVenue) {
                        true -> findNavController().navigate(
                            MainNavigationDirections.navigateToVenueFragment(
                                it
                            )
                        )
                        false -> findNavController().navigate(
                            MainNavigationDirections.navigateToDrinkFragment(
                                it
                            )
                        )
                    }
                    viewModel.onLeft()
                }
            }
        )

        // check loading done and close loading animation
        viewModel.status.observe(
            viewLifecycleOwner, {
                if (it == LoadStatus.DONE) {
                    binding.animationLoading.visibility = View.GONE
                }
            }
        )

        // set location
        viewModel.location = Location.getLocation(requireActivity())

        return binding.root
    }

    private fun getDeviceLocation() {
        try {
            if (isPermissionGranted(AppPermission.AccessFineLocation)) {
                Location.getLocation(requireActivity())
            } else {
                requestPermission(AppPermission.AccessFineLocation)
                getDeviceLocation()
            }
        } catch (e: SecurityException) {
            Logger.d("exception ${e.message}")
        }
    }

}

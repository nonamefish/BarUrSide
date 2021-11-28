package com.mingyuwu.barurside.collect

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.databinding.FragmentCollectPageBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.util.Location
import com.mingyuwu.barurside.util.Logger
import com.mingyuwu.barurside.util.Util
import com.permissionx.guolindev.PermissionX

class CollectPageFragment : Fragment() {

    val isVenue = Util.getString(R.string.collect_tab_is_venue)
    private lateinit var binding: FragmentCollectPageBinding
    private lateinit var mContext: Context
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false
    private val viewModel by viewModels<CollectPageViewModel> {
        getVmFactory(
            this.requireArguments().getBoolean(isVenue)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_collect_page, container, false
        )
        binding.lifecycleOwner = this
        val isVenue = this.requireArguments().getBoolean(isVenue)

        // set variable for get location
        mContext = binding.root.context
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)
        getLocationPermission()

        // set recyclerView adapter and get collect id list
        val adapter = CollectAdapter(
            viewModel,
            CollectAdapter.OnClickListener {
                viewModel.setNavigateToObject(it)
            }
        )

        binding.collectList.adapter = adapter

        viewModel.collectInfo.observe(
            viewLifecycleOwner,
            Observer {
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
            viewLifecycleOwner,
            Observer {
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
            viewLifecycleOwner,
            Observer {
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
            viewLifecycleOwner,
            Observer {
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
            if (locationPermissionGranted) {
                Location.getLocation(requireActivity())
            } else {
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Logger.d("exception ${e.message}")
        }
    }

    // get and check location permission
    private fun getLocationPermission() {
        PermissionX.init(activity)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    Util.getString(R.string.permission_location_collect),
                    Util.getString(R.string.permission_confirm),
                    Util.getString(R.string.permission_reject)
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    Util.getString(R.string.permission_location_collect),
                    Util.getString(R.string.permission_confirm),
                    Util.getString(R.string.permission_reject)
                )
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    locationPermissionGranted = true
                    getDeviceLocation()
                } else {
                    Toast.makeText(
                        mContext,
                        getString(R.string.permission_reject_toast, deniedList),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

}

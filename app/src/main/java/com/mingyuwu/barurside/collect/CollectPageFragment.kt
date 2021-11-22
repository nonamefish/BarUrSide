package com.mingyuwu.barurside.collect

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.MainActivity
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.databinding.FragmentCollectPageBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.map.REQUEST_ENABLE_GPS
import com.permissionx.guolindev.PermissionX

class CollectPageFragment() : Fragment() {

    private lateinit var binding: FragmentCollectPageBinding
    private lateinit var mContext: Context
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false
    private val viewModel by viewModels<CollectPageViewModel> {
        getVmFactory(
            this.requireArguments().getBoolean("isVenue")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_collect_page, container, false
        )
        binding.lifecycleOwner = this
        val isVenue = this.requireArguments().getBoolean("isVenue")

        // set variable for get location
        mContext = binding.root.context
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)

        // get location permission
//        getLocationPermission()

        // set viewModel after get location info
//        (requireActivity() as MainActivity).mlocation.observe(viewLifecycleOwner, Observer {
        // set collect grid adapter
        val adapter = CollectAdapter(
            viewModel,
            CollectAdapter.OnClickListener {
                viewModel.setNavigateToObject(it)
            }
        )

        // set recyclerView adapter and get collect id list
        binding.collectList.adapter = adapter
        viewModel.collectInfo.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.getObjectInfo(isVenue, it)
            } else {
                binding.animationEmpty.visibility = View.VISIBLE
                binding.animationLoading.visibility = View.GONE
            }
        })

        // get collect object information
        viewModel.objectInfo.observe(viewLifecycleOwner, Observer {

            if (it.isEmpty()) {
                binding.animationEmpty.visibility = View.VISIBLE
            } else {
                binding.animationLoading.visibility = View.GONE
                adapter.submitList(it)
            }
        })

        // set navigation to object info page
        viewModel.navigateToObject.observe(viewLifecycleOwner, Observer {
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
        })

        // check loading done and close loading animation
        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it == LoadStatus.DONE) {
                binding.animationLoading.visibility = View.GONE
            }
        })

        // set location
        viewModel.location.value = (requireActivity() as MainActivity).mlocation.value
//        })


        return binding.root
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted
            ) {
                val locationRequest = LocationRequest()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                //更新次數，若沒設定，會持續更新
                locationRequest.numUpdates = 1

                if ((requireActivity() as MainActivity).mlocation.value == null) {
                    // set request Location Updates
                    mFusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult?) {
                                locationResult ?: return
                                (requireActivity() as MainActivity).mlocation.value =
                                    LatLng(
                                        locationResult.lastLocation.latitude,
                                        locationResult.lastLocation.longitude,
                                    )
                            }
                        },
                        null
                    )
                }
            } else {
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Ming: %s", e.message, e)
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
                    "請開通位置存取權，以計算收藏店家與您的距離",
                    "確定",
                    "忍痛拒絕"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "請開通位置存取權，以計算收藏店家與您的距離",
                    "確定",
                    "忍痛拒絕"
                )
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    locationPermissionGranted = true
                    checkGPSState()
                } else {
                    Toast.makeText(
                        mContext,
                        "These permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // check GPS state
    private fun checkGPSState() {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(mContext)
                .setTitle("GPS 尚未開啟")
                .setMessage("使用此功能需要開啟 GPS 定位功能")
                .setPositiveButton("前往開啟",
                    DialogInterface.OnClickListener { _, _ ->
                        startActivityForResult(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_GPS
                        )
                    })
                .setNegativeButton("取消", null)
                .show()
        } else {
            getDeviceLocation()
            Log.d("Ming", "已獲取到位置權限且GPS已開啟，可以準備開始獲取經緯度")
        }
    }
}
package com.mingyuwu.barurside.discoverdetail

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.MainViewModel
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.mockdata.VenueData
import com.mingyuwu.barurside.databinding.FragmentDiscoverDetailBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.map.REQUEST_ENABLE_GPS
import com.mingyuwu.barurside.profile.FriendAdapter
import com.permissionx.guolindev.PermissionX

class DiscoverDetailFragment() : Fragment() {

    private lateinit var mContext: Context
    private lateinit var binding: FragmentDiscoverDetailBinding
    private var locationPermissionGranted = false
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel by viewModels<DiscoverDetailViewModel> {
        getVmFactory(
            DiscoverDetailFragmentArgs.fromBundle(requireArguments()).id?.toList(),
            DiscoverDetailFragmentArgs.fromBundle(requireArguments()).theme,
            DiscoverDetailFragmentArgs.fromBundle(requireArguments()).filterParameter,
        )
    }
    private val mainViewModel by viewModels<MainViewModel> { getVmFactory() }
    private lateinit var adapter: ListAdapter<Any, RecyclerView.ViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val theme = DiscoverDetailFragmentArgs.fromBundle(requireArguments()).theme
        val toolbarTitle = requireActivity().findViewById<TextView>(R.id.text_toolbar_title)

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_discover_detail, container, false
        )
        binding.lifecycleOwner = this
        mContext = binding.root.context
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)

        // set recyclerView adapter
        when (theme) {
            in arrayOf(Theme.RECENT_ACTIVITY, Theme.USER_ACTIVITY) -> {
                toolbarTitle.text = "活動列表"
                adapter = DiscoverActivityAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            Theme.MAP_FILTER -> {
                adapter = DiscoverVenueAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
            }
            Theme.USER_FRIEND -> {
                toolbarTitle.text = "朋友列表"
                adapter = FriendAdapter(viewModel)
                binding.discoverObjectList.layoutManager =
                    GridLayoutManager(binding.root.context, 3)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            Theme.NOTIFICATION -> {
                toolbarTitle.text = "通知"
                adapter = NotificationAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            Theme.AROUND_VENUE -> {
                viewModel.mLocation = mainViewModel.location

                if (mainViewModel.location.value == null) {
                    getLocationPermission()
                }

                adapter = DiscoverVenueAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility

                viewModel.mLocation.observe(viewLifecycleOwner, Observer {
                    viewModel.getAroundVenue(it)
                })
            }
            in arrayOf(Theme.HOT_VENUE, Theme.HIGH_RATE_VENUE) -> {
                adapter = DiscoverVenueAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            in arrayOf(Theme.HOT_DRINK, Theme.HIGH_RATE_DRINK, Theme.VENUE_MENU) -> {
                adapter = DiscoverDrinkAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
        }

        // click info button and navigate to info fragment
        viewModel.navigateToInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    is Venue -> {
                        findNavController().navigate(
                            MainNavigationDirections.navigateToVenueFragment(it.id)
                        )
                    }
                    is Drink -> {
                        findNavController().navigate(
                            MainNavigationDirections.navigateToDrinkFragment(it.id)
                        )
                    }
                    is Activity -> {
                        findNavController().navigate(
                            MainNavigationDirections.navigateToActivityDetailDialog(it, theme)
                        )
                    }
                }
                viewModel.onLeft()
            }
        })

        // assign value to recyclerView
        viewModel.detailData.observe(viewLifecycleOwner, Observer { it ->
            Log.d("Ming", "detailData: $it")
            if (theme == Theme.NOTIFICATION) {
                val list = (it as List<Notification>).filter { notifications ->
                    notifications.toId == UserManager.user.value?.id ?: ""
                }.take(20)
                adapter.submitList(list)
            } else {
                adapter.submitList(it)
            }
        })

        // set random button click listener
        binding.btnRandom.setOnClickListener {
            viewModel.detailData.value?.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToRandomFragment((it as List<Venue>).toTypedArray())
                )
            }
        }

        return binding.root
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
                    "請開通位置存取權，以提供您所在位置附近的優質酒館",
                    "確定",
                    "忍痛拒絕"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "請開通位置存取權，以提供您所在位置附近的優質酒館",
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

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted
            ) {
                val locationRequest = LocationRequest()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                //更新次數，若沒設定，會持續更新
                locationRequest.numUpdates = 1

                // set request Location Updates
                mFusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            locationResult ?: return
                            mainViewModel.location.value =
                                LatLng(
                                    locationResult.lastLocation.latitude,
                                    locationResult.lastLocation.longitude
                                )
                        }

                    },
                    null
                )
            } else {
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Ming: %s", e.message, e)
        }
    }
}


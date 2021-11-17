package com.mingyuwu.barurside.discoverdetail

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.MainActivity
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.MainViewModel
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.databinding.FragmentDiscoverDetailBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.map.REQUEST_ENABLE_GPS
import com.mingyuwu.barurside.profile.FriendAdapter
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.util.Util
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
        val ids = DiscoverDetailFragmentArgs.fromBundle(requireArguments()).id?.toList()
        val theme = DiscoverDetailFragmentArgs.fromBundle(requireArguments()).theme

        val toolbarTitle = (requireActivity() as MainActivity).viewModel.discoverType
        toolbarTitle.value = when (theme) {
            in arrayOf(Theme.RECENT_ACTIVITY, Theme.USER_ACTIVITY) -> "活動列表"
            Theme.USER_FRIEND -> "朋友列表"
            Theme.NOTIFICATION -> "通知"
            Theme.VENUE_MENU -> "菜單"
            Theme.IMAGES -> "相片"
            else -> "搜尋結果"
        }


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
                adapter = DiscoverActivityAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            Theme.MAP_FILTER -> {
                adapter = DiscoverVenueAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
            }
            Theme.USER_FRIEND -> {
                adapter = FriendAdapter(viewModel)
                binding.discoverObjectList.layoutManager =
                    GridLayoutManager(binding.root.context, 3)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            Theme.NOTIFICATION -> {
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
            Theme.IMAGES -> {
                adapter = ImageAdapter(240, 220)
                binding.discoverObjectList.layoutManager =
                    GridLayoutManager(binding.root.context, 2)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
                binding.animationLoading.visibility = View.GONE
                adapter.submitList(ids)
            }
            in arrayOf(Theme.HOT_VENUE, Theme.HIGH_RATE_VENUE) -> {
                adapter = DiscoverVenueAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            in arrayOf(Theme.HOT_DRINK, Theme.HIGH_RATE_DRINK) -> {
                adapter = DiscoverDrinkAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            Theme.VENUE_MENU -> {
                adapter = DiscoverDrinkAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.setImageResource(R.drawable.ic_baseline_add_24)
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
            if (it.isNullOrEmpty()) { // adapter list is empty then finish loading animation

                binding.animationEmpty.visibility = View.VISIBLE
                binding.animationLoading.visibility = View.GONE
            } else {
                var list: List<Any>?
                // set notification value
                if (theme == Theme.NOTIFICATION) {
                    list = (it as List<Notification>).filter { ntfys->
                        ntfys.toId == UserManager.user.value!!.id &&
                            ((ntfys.type == "friend" && ntfys.isCheck == false) ||
                                    (ntfys.type == "activity" && Util.getDiffHour(ntfys.timestamp!!) < 24))
                    }.take(20)
                    if (!list.isNullOrEmpty()) {
                        viewModel.checkNotification(list.map { it.id })
                    }
                } else {
                    list = it
                }

                // submit list and set loading and empty animation
                if (list.isEmpty()) {
                    binding.animationEmpty.visibility = View.VISIBLE
                } else {
                    adapter.submitList(list)
                }
                binding.animationLoading.visibility = View.GONE
            }
        })

        // set random button click listener
        binding.btnRandom.setOnClickListener {
            when (theme) {
                Theme.MAP_FILTER -> {
                    viewModel.detailData.value?.let {
                        findNavController().navigate(
                            MainNavigationDirections.navigateToRandomFragment((it as List<Venue>).toTypedArray())
                        )
                    }
                }
                Theme.VENUE_MENU -> {
                    ids?.get(0)?.let {
                        findNavController().navigate(
                            MainNavigationDirections.navigateToAddDrinkFragment(it)
                        )
                    }
                }
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


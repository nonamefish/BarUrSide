package com.mingyuwu.barurside.map

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
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentMapBinding
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.*
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.MainViewModel
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.ext.getVmFactory
import com.permissionx.guolindev.PermissionX


const val REQUEST_LOCATION_PERMISSION = 1
const val REQUEST_ENABLE_GPS = 2

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var mContext: Context
    private lateinit var parent: ViewGroup
    private lateinit var binding: FragmentMapBinding
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false
    private val viewModel by viewModels<MapViewModel> { getVmFactory() }
    private val mainViewModel by viewModels<MainViewModel> { getVmFactory() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_map, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        if (container != null) {
            parent = container
        }
        mContext = binding.root.context
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)

        val mapFragment =
            childFragmentManager.findFragmentById(binding.googleMap.id) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // set filter button on click listener
        binding.btnFilter.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToFilterFragment()
            )
        }

        // search info: set auto completed text adapter
        viewModel.searchInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("Ming", "searchInfo: $it")
                val venueList = it.map { venue -> venue.name }
                val adapter = ArrayAdapter(
                    binding.root.context,
                    android.R.layout.simple_spinner_dropdown_item,
                    venueList!!
                )
                binding.autoMapFilter.setAdapter(adapter)

                binding.autoMapFilter.setOnItemClickListener { parent, _, position, _ ->
                    val selected = parent.getItemAtPosition(position)
                    val pos = venueList.indexOf(selected)
                    binding.autoMapFilter.setText("")
                    Log.d("Ming", "selected venue: ${it[pos]}")
                    addMapMark(it[pos], true)
                }
            }
        })

        // search venue after autocompleted text
        viewModel.searchText.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.length == 1) {
                    viewModel.getVenueBySearch(it)
                }
            }
        })

        // init : nearby venue list
        viewModel.venueList.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("Ming", "venueList: $it")
                for (item in it) {
                    addMapMark(item, false)
                }
            }
        })

        // navigate to Venue
        viewModel.navigateToVenue.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(MainNavigationDirections.navigateToVenueFragment(it))
            }
        })

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // set google map
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        // set info window
        mMap?.setInfoWindowAdapter(MapInfoWindowAdapter(mContext, viewModel, parent))
        mMap?.setOnInfoWindowClickListener(this)

        // get location btn layout parameter
        val mapView = binding.googleMap
        val locationButton = mapView.findViewById<View>("2".toInt())
        val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams

        // set location btn margin
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 280)

        getLocationPermission()
    }


    private fun addMapMark(venue: Venue, isSelected: Boolean) {
        if (isSelected) {
            mMap.clear()
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(venue.latitude, venue.longitude), 15f
                )
            )
        }

        mMap?.addMarker(
            MarkerOptions()
                .position(LatLng(venue.latitude, venue.longitude))
                .title(venue.name)
                .snippet(
                    "${venue.id}," +
                            "${venue.avgRating}," +
                            "${venue.rtgCount}," +
                            "${venue.images?.get(0)}"
                )
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_64px_beer))
        )
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted
            ) {
                val locationRequest = LocationRequest()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//                //更新頻率
//                locationRequest.interval = 1000
                //更新次數，若沒設定，會持續更新
                locationRequest.numUpdates = 1

                // set request Location Updates
                mFusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            locationResult ?: return
                            mainViewModel.location.value =
                                LatLng(25.04265289103591, 121.565102094742)
//
//                                LatLng(
//                                    locationResult.lastLocation.latitude,
//                                    locationResult.lastLocation.longitude
//                                )




                            // get near bar
                            viewModel.getVenueByLocation(mainViewModel.location.value!!)

                            // google map current location (blue point)
                            mMap.isMyLocationEnabled = true
                            mMap.uiSettings.isMyLocationButtonEnabled = true
                            mMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    mainViewModel.location.value, 15f
                                )
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
//                    Toast.makeText(mContext, "All permissions are granted", Toast.LENGTH_LONG).show()
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

    override fun onInfoWindowClick(marker: Marker?) {
        marker?.title?.let { title ->
            val info = marker.snippet.toString().split(",")
            viewModel.navigateToVenue.value = info[0]
        }
    }
}
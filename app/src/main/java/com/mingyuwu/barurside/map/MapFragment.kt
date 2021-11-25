package com.mingyuwu.barurside.map

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mingyuwu.barurside.MainActivity
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.databinding.FragmentMapBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.util.Logger
import com.mingyuwu.barurside.util.Util
import com.permissionx.guolindev.PermissionX

const val REQUEST_ENABLE_GPS = 2

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var mContext: Context
    private lateinit var parent: ViewGroup
    private lateinit var binding: FragmentMapBinding
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false
    private val viewModel by viewModels<MapViewModel> { getVmFactory() }

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

        // set variable for get location info
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
                val venueList = it.map { venue -> venue.name }
                val adapter = ArrayAdapter(
                    binding.root.context,
                    android.R.layout.simple_spinner_dropdown_item,
                    venueList
                )
                binding.autoMapFilter.setAdapter(adapter)

                binding.autoMapFilter.setOnItemClickListener { parent, _, position, _ ->
                    val selected = parent.getItemAtPosition(position)
                    val pos = venueList.indexOf(selected)
                    binding.autoMapFilter.setText("")
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
                for (item in it) {
                    addMapMark(item, false)
                }
            }
        })

        // navigate to Venue
        viewModel.navigateToVenue.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(MainNavigationDirections.navigateToVenueFragment(it))
                viewModel.onLeft()
            }
        })

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Logger.d("onMapReady")

        // set google map
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        // set info window
        mMap.setInfoWindowAdapter(MapInfoWindowAdapter(mContext, viewModel, parent))
        mMap.setOnInfoWindowClickListener(this)

        // get location btn layout parameter
        val mapView = binding.googleMap
        val locationButton = mapView.findViewById<View>("2".toInt())

        locationButton?.let {
            val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams

            // set location btn margin
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            rlp.setMargins(0, 0, 30, 280)
        }

        getLocationPermission()
    }

    private fun addMapMark(venue: Venue, isSelected: Boolean) {
        try {
            if (isSelected) {
                mMap.clear()
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(venue.latitude, venue.longitude), 15f
                    )
                )
            }

            val image = if (!venue.images.isNullOrEmpty()) {
                venue.images?.get(0)
            } else ""

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(venue.latitude, venue.longitude))
                    .title(venue.name)
                    .snippet(
                        "${venue.id}," +
                                "${venue.avgRating}," +
                                "${venue.rtgCount}," +
                                "$image"
                    )
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_mark_wine))
            )
        } catch (e: Exception) {
            Logger.d("addMapMark: ${e.message}")
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {

                mFusedLocationProviderClient.lastLocation
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            // google map current location (blue point)
                            val location = task.result
                            (requireActivity() as MainActivity).location.value =
                                LatLng(location.latitude, location.longitude)
                            // get near bar
                            viewModel.getVenueByLocation((requireActivity() as MainActivity).location.value!!)

                            // set map current location and icon
                            mMap.isMyLocationEnabled = true
                            mMap.uiSettings?.isMyLocationButtonEnabled = true
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    (requireActivity() as MainActivity).location.value, 15f
                                )
                            )
                        }
                    }
            } else {
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Logger.e("SecurityException ${e.message}")
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
                    checkGPSState()
                } else {
                    Toast.makeText(
                        mContext,
                        getString(R.string.permission_reject_toast, deniedList),
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
            AlertDialog.Builder(mContext)
                .setTitle(Util.getString(R.string.request_gps_title))
                .setMessage(Util.getString(R.string.request_gps_content))
                .setPositiveButton(
                    Util.getString(R.string.request_gps_positive)
                ) { _, _ ->
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_GPS,
                    )
                }
                .setNegativeButton(Util.getString(R.string.request_gps_cancel), null)
                .show()
        } else {
            getDeviceLocation()
        }
    }

    override fun onInfoWindowClick(marker: Marker?) {
        marker?.title?.let {
            val info = marker.snippet.toString().split(",")
            viewModel.navigateToVenue.value = info[0]
        }
    }

}
package com.mingyuwu.barurside.discoverdetail

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
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.MainActivity
import com.mingyuwu.barurside.MainNavigationDirections
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
import com.mingyuwu.barurside.util.Location
import com.mingyuwu.barurside.util.Logger
import com.mingyuwu.barurside.util.Util
import com.permissionx.guolindev.PermissionX

class DiscoverDetailFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var binding: FragmentDiscoverDetailBinding
    private var locationPermissionGranted = false
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var adapter: ListAdapter<Any, RecyclerView.ViewHolder>
    private val viewModel by viewModels<DiscoverDetailViewModel> {
        getVmFactory(
            DiscoverDetailFragmentArgs.fromBundle(requireArguments()).id?.toList(),
            DiscoverDetailFragmentArgs.fromBundle(requireArguments()).theme,
            DiscoverDetailFragmentArgs.fromBundle(requireArguments()).filterParameter,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ids = DiscoverDetailFragmentArgs.fromBundle(requireArguments()).id?.toList()
        val theme = DiscoverDetailFragmentArgs.fromBundle(requireArguments()).theme

        // set tool bar title with different theme
        val toolbarTitle = (requireActivity() as MainActivity).viewModel.discoverType
        toolbarTitle.value = when (theme) {
            in arrayOf(
                Theme.RECENT_ACTIVITY,
                Theme.USER_ACTIVITY
            ) -> Util.getString(R.string.activity_list_title)
            Theme.USER_FRIEND -> Util.getString(R.string.user_friend_title)
            Theme.NOTIFICATION -> Util.getString(R.string.notification_title)
            Theme.VENUE_MENU -> Util.getString(R.string.menu_title)
            Theme.IMAGES -> Util.getString(R.string.image_title)
            Theme.HOT_VENUE -> Util.getString(R.string.trend_bar)
            Theme.HOT_VENUE -> Util.getString(R.string.trend_drink)
            Theme.HIGH_RATE_VENUE -> Util.getString(R.string.high_rate_bar)
            Theme.HIGH_RATE_DRINK -> Util.getString(R.string.high_rate_drink)
            Theme.AROUND_VENUE -> Util.getString(R.string.around_bar)
            else -> Util.getString(R.string.filter_title)
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
                viewModel.location = Location.getLocation(requireActivity())

                if (viewModel.location.value == null) {
                    getLocationPermission()
                }
                adapter = DiscoverVenueAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility

                viewModel.location.observe(
                    viewLifecycleOwner,
                    Observer {
                        viewModel.getAroundVenue(it)
                    }
                )
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
            else -> {
                Logger.w("Wrong Theme: $theme")
            }
        }

        // click info button and navigate to info fragment
        viewModel.navigateToInfo.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    when (it) {
                        is Venue -> {
                            findNavController().navigate(
                                MainNavigationDirections.navigateToVenueFragment(it.id)
                            )
                            viewModel.onLeft()
                        }
                        is Drink -> {
                            findNavController().navigate(
                                MainNavigationDirections.navigateToDrinkFragment(it.id)
                            )
                            viewModel.onLeft()
                        }
                        is Activity -> {
                            findNavController().navigate(
                                MainNavigationDirections.navigateToActivityDetailDialog(it, null, theme)
                            )
                            viewModel.onLeft()
                        }
                    }
                }
            }
        )

        // assign value to recyclerView
        viewModel.detailData.observe(
            viewLifecycleOwner,
            Observer { it ->
                if (it.isNullOrEmpty()) {
                    // finish loading and close lottie
                    binding.animationEmpty.visibility = View.VISIBLE
                    binding.animationLoading.visibility = View.GONE
                } else {

                    val list: List<Any>?

                    // set notification value
                    if (theme == Theme.NOTIFICATION) {

                        list = (it as List<Notification>).filter {
                            it.toId == UserManager.user.value!!.id
                        }.take(20)

                        if (!it.isNullOrEmpty()) {
                            it.filter { it.isCheck == false }.map { it.id }.let {
                                viewModel.checkNotification(it)
                            }
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
            }
        )

        // set random button click listener
        binding.btnRandom.setOnClickListener {
            when (theme) {
                Theme.MAP_FILTER -> {
                    viewModel.detailData.value?.let {
                        it?.let{
                            findNavController().navigate(
                                MainNavigationDirections.navigateToRandomFragment(
                                    (it as List<Venue>).toTypedArray()
                                )
                            )
                        }
                    }
                }
                Theme.VENUE_MENU -> {
                    ids?.get(0)?.let {
                        findNavController().navigate(
                            MainNavigationDirections.navigateToAddDrinkFragment(it)
                        )
                    }
                }
                else -> {
                }
            }
        }

        return binding.root
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted
            ) {
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

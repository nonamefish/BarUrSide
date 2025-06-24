package com.mingyuwu.barurside.discoverdetail

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.MainActivity
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.databinding.FragmentDiscoverDetailBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.ext.isPermissionGranted
import com.mingyuwu.barurside.ext.requestPermission
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.profile.FriendAdapter
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.util.AppPermission
import com.mingyuwu.barurside.util.Location
import com.mingyuwu.barurside.util.Logger
import com.mingyuwu.barurside.util.Util

class DiscoverDetailFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var binding: FragmentDiscoverDetailBinding
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
        savedInstanceState: Bundle?,
    ): View {
        val ids = DiscoverDetailFragmentArgs.fromBundle(requireArguments()).id?.toList()
        val theme = DiscoverDetailFragmentArgs.fromBundle(requireArguments()).theme

        // set tool bar title with different theme
        val toolbarTitle = (requireActivity() as MainActivity).viewModel.discoverType
        toolbarTitle.value = when (theme) {
            in arrayOf(
                Theme.RECENT_ACTIVITY,
                Theme.USER_ACTIVITY
            ),
            -> Util.getString(R.string.activity_list_title)
            Theme.USER_FRIEND -> Util.getString(R.string.user_friend_title)
            Theme.NOTIFICATION -> Util.getString(R.string.notification_title)
            Theme.VENUE_MENU -> Util.getString(R.string.menu_title)
            Theme.IMAGES -> Util.getString(R.string.image_title)
            Theme.HOT_VENUE -> Util.getString(R.string.trend_bar)
            Theme.HOT_DRINK -> Util.getString(R.string.trend_drink)
            Theme.HIGH_RATE_VENUE -> Util.getString(R.string.high_rate_bar)
            Theme.HIGH_RATE_DRINK -> Util.getString(R.string.high_rate_drink)
            Theme.AROUND_VENUE -> Util.getString(R.string.around_bar)
            else -> Util.getString(R.string.filter_title)
        }

        // Inflate the layout for this fragment
        binding = FragmentDiscoverDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        mContext = binding.root.context

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
                    getDeviceLocation()
                }
                adapter = DiscoverVenueAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility

                viewModel.location.observe(
                    viewLifecycleOwner, {
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
            viewLifecycleOwner, {
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
                                MainNavigationDirections.navigateToActivityDetailDialog(it,
                                    null,
                                    theme)
                            )
                            viewModel.onLeft()
                        }
                    }
                }
            }
        )

        // assign value to recyclerView
        viewModel.detailData.observe(
            viewLifecycleOwner, { it ->
                if (it.isNullOrEmpty()) {
                    // finish loading and close lottie
                    binding.animationEmpty.visibility = View.VISIBLE
                    binding.animationLoading.visibility = View.GONE
                } else {

                    val list: List<Any>?

                    // set notification value
                    if (theme == Theme.NOTIFICATION) {

                        list = it.filterIsInstance<Notification>().filter {
                            it.toId == UserManager.user.value!!.id
                        }.take(20)

                        if (!it.isNullOrEmpty()) {
                            it.filterIsInstance<Notification>()
                                .filter { it.isCheck == false }.map { it.id }.let {
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
                        it.let {
                            findNavController().navigate(
                                MainNavigationDirections.navigateToRandomFragment(
                                    it.filterIsInstance<Venue>().toTypedArray()
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

    private fun reportUser() {
        // set alert dialog view
        val alertDialog = AlertDialog.Builder(binding.root.context)
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_rating_uncompleted, null)
        alertDialog.setView(mView)
        val dialog = alertDialog.create()

        mView.let{
            // set dialog content
            val txtDialog = it.findViewById<TextView>(R.id.dialog_content)
            val titleDialog = it.findViewById<TextView>(R.id.dialog_title)
            titleDialog.text = getString(R.string.report_title)
            txtDialog.text = getString(R.string.report_text_2)

            // set button click listener
            val btDialog = it.findViewById<Button>(R.id.button_confirm)
            btDialog.text = getString(R.string.report_confirm)
            btDialog.setOnClickListener { dialog.dismiss() }
        }

        dialog.show()

        // set border as transparent
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 900
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}

package com.mingyuwu.barurside.discoverdetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.mockdata.VenueData
import com.mingyuwu.barurside.databinding.FragmentDiscoverDetailBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.profile.FriendAdapter

class DiscoverDetailFragment() : Fragment() {

    private lateinit var binding: FragmentDiscoverDetailBinding
    private val viewModel by viewModels<DiscoverDetailViewModel> {
        getVmFactory(
            DiscoverDetailFragmentArgs.fromBundle(requireArguments()).theme,
            DiscoverDetailFragmentArgs.fromBundle(requireArguments()).filterParameter,
        )
    }
    private lateinit var adapter: ListAdapter<Any, RecyclerView.ViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val theme = DiscoverDetailFragmentArgs.fromBundle(requireArguments()).theme

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_discover_detail, container, false
        )
        binding.lifecycleOwner = this


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
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            Theme.NOTIFICATION -> {
                adapter = NotificationAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            in arrayOf(Theme.AROUND_VENUE, Theme.HOT_VENUE, Theme.HIGH_RATE_VENUE) -> {
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
                        MainNavigationDirections.navigateToActivityDetailDialog(it)
                    )
                }
            }
        }
        )

        // assign value to recyclerView
        viewModel.detailData.observe(viewLifecycleOwner, Observer {
            Log.d("Ming","detailData: $it")
            adapter.submitList(it)
        }
        )

        // set random button click listener
        binding.btnRandom.setOnClickListener {
            findNavController().navigate(MainNavigationDirections.navigateToRandomFragment(VenueData.venue.venue[0]))
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}
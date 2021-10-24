package com.mingyuwu.barurside.discoverdetail

import android.os.Bundle
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
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.mockdata.VenueData
import com.mingyuwu.barurside.databinding.FragmentDiscoverDetailBinding
import com.mingyuwu.barurside.ext.getVmFactory

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
        when (theme.order) {
            1 -> {
                adapter = DiscoverActivityAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            6 -> {
                adapter = DiscoverVenueAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
            }
            in arrayOf(0, 3, 5) -> {
                adapter = DiscoverVenueAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
            in arrayOf(2, 4) -> {
                adapter = DiscoverDrinkAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
                binding.btnRandom.visibility = View.GONE // set random button invisibility
            }
        }

        // assign value to recyclerView
        viewModel.detailData.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        }
        )

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
            }
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
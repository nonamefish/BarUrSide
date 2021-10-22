package com.mingyuwu.barurside.discoverdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentDiscoverDetailBinding
import com.mingyuwu.barurside.ext.getVmFactory

class DiscoverDetailFragment() : Fragment() {

    private lateinit var binding: FragmentDiscoverDetailBinding
    private val viewModel by viewModels<DiscoverDetailViewModel> {
        getVmFactory(
            DiscoverDetailFragmentArgs.fromBundle(requireArguments()).theme
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
                adapter = DiscoverActivityAdapter()
                binding.discoverObjectList.adapter = adapter
            }
            in arrayOf(0, 3, 5) -> {
                adapter = DiscoverVenueAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
            }
            else -> {
                adapter = DiscoverDrinkAdapter(viewModel)
                binding.discoverObjectList.adapter = adapter
            }
        }

        // assign value to recyclerView
        viewModel.detailData.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            }
        )

        // Inflate the layout for this fragment
        return binding.root
    }
}
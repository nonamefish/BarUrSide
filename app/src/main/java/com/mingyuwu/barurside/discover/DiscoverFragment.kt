package com.mingyuwu.barurside.discover

import com.mingyuwu.barurside.R
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.databinding.FragmentDiscoverBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.map.MapViewModel

class DiscoverFragment : Fragment() {

    private val viewModel by viewModels<DiscoverViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentDiscoverBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //set spinner type and adapter
        val adapter = ArrayAdapter.createFromResource(
            binding.root.context,
            R.array.search_type,
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spinnerSearchType.adapter = adapter
        binding.spinnerSearchType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    viewModel.searchType.value = when (position) {
                        0 -> false
                        1 -> true
                        else -> null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // observe search type
        viewModel.searchType.observe(viewLifecycleOwner, Observer {
            binding.autoDiscoverFilter.setText("")
            viewModel.searchInfo.value = null
        })

        // search venue after autocompleted text
        viewModel.searchText.observe(viewLifecycleOwner, Observer
        {
            it?.let {
                if (it.isNotEmpty()) {
                    if (viewModel.searchInfo.value == null) {
                        when (viewModel.searchType.value) {
                            true -> viewModel.getDrinkBySearch(it)
                            false -> viewModel.getVenueBySearch(it)
                        }
                    }
                } else {
                    viewModel.searchInfo.value = null
                }
            }
        })

        // search info: set auto completed text adapter
        viewModel.searchInfo.observe(viewLifecycleOwner, Observer
        {
            it?.let {

                val list = when (viewModel.searchType.value) {
                    true -> (it as List<Drink>).map { drink -> drink.name }
                    false -> (it as List<Venue>).map { venue -> venue.name }
                    else -> listOf()
                }

                val id = when (viewModel.searchType.value) {
                    true -> (it as List<Drink>).map { drink -> drink.id }
                    false -> (it as List<Venue>).map { venue -> venue.id }
                    else -> listOf()
                }

                // set adapter
                val adapter = ArrayAdapter(
                    binding.root.context,
                    android.R.layout.simple_spinner_dropdown_item,
                    list!!
                )
                binding.autoDiscoverFilter.setAdapter(adapter)

                // auto complete text click listener
                binding.autoDiscoverFilter.setOnItemClickListener { parent, _, position, _ ->
                    val selected = parent.getItemAtPosition(position)
                    val pos = list.indexOf(selected)
                    binding.autoDiscoverFilter.setText("")

                    // navigate
                    viewModel.setNavigateToObject(id[pos])
                }
            }
        })

        // navigate to object info
        viewModel.navigateToObject.observe(viewLifecycleOwner, Observer
        {
            it?.let {
                when (viewModel.searchType.value) {
                    true -> {
                        findNavController().navigate(
                            MainNavigationDirections.navigateToDrinkFragment(it)
                        )
                    }
                    false -> {
                        findNavController().navigate(
                            MainNavigationDirections.navigateToVenueFragment(it)
                        )
                    }
                }
                viewModel.onLeft()
            }
        })

        // navigate to theme
        viewModel.navigateToTheme.observe(viewLifecycleOwner, Observer
        {
            it?.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToDiscoverDetailFragment(
                        it, null, null
                    )
                )
                viewModel.onLeft()
            }
        })

        return binding.root
    }
}
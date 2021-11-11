package com.mingyuwu.barurside.collect

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.databinding.FragmentCollectPageBinding
import com.mingyuwu.barurside.ext.getVmFactory

class CollectPageFragment() : Fragment() {

    private lateinit var binding: FragmentCollectPageBinding
    private val viewModel by viewModels<CollectPageViewModel> {
        getVmFactory(
            this.requireArguments().getBoolean("isVenue")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_collect_page, container, false
        )
        binding.lifecycleOwner = this

        val isVenue = this.requireArguments().getBoolean("isVenue")

        // set collect grid adapter
        val adapter = CollectAdapter(
            viewModel,
            CollectAdapter.OnClickListener {
                viewModel.setNavigateToObject(it)
            }
        )

        // set recyclerView adapter and get collect id list
        binding.collectList.adapter = adapter
        viewModel.collectInfo.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.getObjectInfo(isVenue, it)
            } else {
                binding.animationEmpty.visibility = View.VISIBLE
                binding.animationLoading.visibility = View.GONE
            }
        })

        // get collect object information
        viewModel.objectInfo.observe(viewLifecycleOwner, Observer {

            if (it.isEmpty()) {
                binding.animationEmpty.visibility = View.VISIBLE
            } else {
                binding.animationLoading.visibility = View.GONE
                adapter.submitList(it)
            }
        })

        // set navigation to object info page
        viewModel.navigateToObject.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (isVenue) {
                    true -> findNavController().navigate(
                        MainNavigationDirections.navigateToVenueFragment(
                            it
                        )
                    )
                    false -> findNavController().navigate(
                        MainNavigationDirections.navigateToDrinkFragment(
                            it
                        )
                    )
                }
                viewModel.onLeft()
            }
        })

        // check loading done and close loading animation
        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it == LoadStatus.DONE) {
                binding.animationLoading.visibility = View.GONE
            }
        })

        return binding.root
    }
}
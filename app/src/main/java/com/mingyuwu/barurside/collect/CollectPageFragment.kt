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

        binding.collectList.adapter = adapter
        viewModel.collectInfo.observe(viewLifecycleOwner, Observer {
            Log.d("Ming","$isVenue collect: $it")
            if(!it.isNullOrEmpty()){
                viewModel.getObjectInfo(isVenue, it)
            }
        })

        viewModel.objectInfo.observe(viewLifecycleOwner, Observer {
            it?.let{
                adapter.submitList(it)
            }
        })

        // set navigation to object info page
        viewModel.navigateToObject.observe(viewLifecycleOwner, Observer {
            it?.let{
                when(isVenue){
                    true->findNavController().navigate(MainNavigationDirections.navigateToVenueFragment(it))
                    false->findNavController().navigate(MainNavigationDirections.navigateToDrinkFragment(it))
                }
                viewModel.onLeft()
            }
        })

        return binding.root
    }
}
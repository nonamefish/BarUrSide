package com.mingyuwu.barurside.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentActivityPageBinding
import com.mingyuwu.barurside.discoverdetail.*
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.rating.InfoRatingAdapter

class ActivityPageFragment() : Fragment() {

    private lateinit var binding: FragmentActivityPageBinding
    private lateinit var adapter: ListAdapter<Any, RecyclerView.ViewHolder>
    private val viewModel by viewModels<ActivityPageViewModel> {
        getVmFactory(
            requireArguments().get("type") as ActivityTypeFilter
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_activity_page, container, false
        )

        // get bundle data
        val type = this.requireArguments().get("type")

        // set recyclerView adapter
        when (type) {
            ActivityTypeFilter.RECOMMEND -> {
                adapter = InfoRatingAdapter()
                binding.activityList.adapter = adapter
                binding.btnAddActivity.visibility = View.GONE
            }
            ActivityTypeFilter.ACTIVITY -> {
                adapter = DiscoverActivityAdapter()
                binding.activityList.adapter = adapter
            }
            ActivityTypeFilter.FOLLOW -> {
                adapter = InfoRatingAdapter()
                binding.activityList.adapter = adapter
                binding.btnAddActivity.visibility = View.GONE
            }
        }

        // assign value to recyclerView
        viewModel.activityData.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            }
        )

        return binding.root
    }
}
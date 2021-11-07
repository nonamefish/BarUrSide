package com.mingyuwu.barurside.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.databinding.FragmentActivityPageBinding
import com.mingyuwu.barurside.discover.Theme
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
                adapter = DiscoverActivityAdapter(viewModel)
                binding.activityList.adapter = adapter
            }
            ActivityTypeFilter.FOLLOW -> {
                adapter = InfoRatingAdapter()
                binding.activityList.adapter = adapter
                binding.btnAddActivity.visibility = View.GONE
            }
        }


        // assign value to recyclerView
        viewModel.listDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        // navigate to detail fragment
        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Activity -> {
                    findNavController().navigate(
                        MainNavigationDirections.navigateToActivityDetailDialog(
                            it,
                            Theme.NONE
                        )
                    )
                }
            }
        })

        // set user data
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (type == ActivityTypeFilter.FOLLOW) {
                viewModel.getRatingByFriend(it.id)
            }
        })

        // set add activity on click listener
        binding.btnAddActivity.setOnClickListener {
            findNavController().navigate(MainNavigationDirections.navigateToAddActivityFragment())
        }


        return binding.root
    }

}
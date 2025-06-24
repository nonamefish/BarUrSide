package com.mingyuwu.barurside.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.databinding.FragmentActivityPageBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.discoverdetail.DiscoverActivityAdapter
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.rating.InfoRatingAdapter
import com.mingyuwu.barurside.util.Util

class ActivityPageFragment : Fragment() {

    private lateinit var binding: FragmentActivityPageBinding
    private lateinit var adapter: ListAdapter<Any, RecyclerView.ViewHolder>
    private val type = Util.getString(R.string.activity_tab_type)
    private val viewModel by viewModels<ActivityPageViewModel> {
        getVmFactory(
            requireArguments().get(type) as ActivityTypeFilter
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentActivityPageBinding.inflate(inflater, container, false)

        // get activity tab type and set recyclerView adapter
        val type = this.requireArguments().get(type)

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
        viewModel.listDate.observe(
            viewLifecycleOwner, {
                it?.let {
                    if (it.isEmpty()) {
                        binding.animationEmpty.visibility = View.VISIBLE
                    } else {
                        adapter.submitList(it)
                    }
                }
            }
        )

        // navigate to detail fragment
        viewModel.navigateToDetail.observe(
            viewLifecycleOwner, {
                it?.let {
                    when (it) {
                        is Activity -> {
                            findNavController().navigate(
                                MainNavigationDirections.navigateToActivityDetailDialog(
                                    it,
                                    null,
                                    Theme.NONE
                                )
                            )
                            viewModel.onLeft()
                        }
                    }
                }
            }
        )

        // get friend data when activity pade is follow
        viewModel.user.observe(
            viewLifecycleOwner, {
                if (type == ActivityTypeFilter.FOLLOW) {
                    viewModel.getRatingByFriend(true, it.id)
                }
            }
        )

        // set add activity on click listener
        binding.btnAddActivity.setOnClickListener {
            findNavController().navigate(MainNavigationDirections.navigateToAddActivityFragment())
        }

        // check loading done and close loading animation
        viewModel.status.observe(
            viewLifecycleOwner, {
                if (it == LoadStatus.DONE) {
                    binding.animationLoading.visibility = View.GONE
                }
            }
        )

        return binding.root
    }
}

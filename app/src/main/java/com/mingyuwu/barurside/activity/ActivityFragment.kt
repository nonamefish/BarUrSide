package com.mingyuwu.barurside.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.collect.CollectViewPageAdapter
import com.mingyuwu.barurside.databinding.FragmentActivityBinding
import com.mingyuwu.barurside.databinding.FragmentDiscoverBinding

class ActivityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentActivityBinding.inflate(layoutInflater)

        // set tab and pager
        binding.viewPagerMain.adapter = ActivityViewPageAdapter(this)
        TabLayoutMediator(binding.tabLayoutMain, binding.viewPagerMain) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.activity_tab_recommend)
                1 -> tab.text = getString(R.string.activity_tab_activity)
                2 -> tab.text = getString(R.string.activity_tab_follow)
            }
        }.attach()

        // Inflate the layout for this fragment
        return binding.root
    }

}
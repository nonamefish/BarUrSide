package com.mingyuwu.barurside.collect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentCollectBinding

class CollectFragment : Fragment() {
    private lateinit var binding: FragmentCollectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_collect, container, false
        )

        // set tab and pager
        binding.viewPagerMain.adapter = CollectViewPageAdapter(this)
        TabLayoutMediator(binding.tabLayoutMain, binding.viewPagerMain) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.collect_tab_venue)
                1 -> tab.text = getString(R.string.collect_tab_drink)
            }
        }.attach()

        return binding.root
    }

}
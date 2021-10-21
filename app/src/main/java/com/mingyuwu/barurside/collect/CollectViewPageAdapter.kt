package com.mingyuwu.barurside.collect

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class CollectViewPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> getPageFragment(bundleOf("isVenue" to true))
            1 -> getPageFragment(bundleOf("isVenue" to false))
            else -> throw Exception("Unknown position $position")
        }
    }

    private fun getPageFragment(bundle: Bundle): CollectPageFragment {
        val fragment = CollectPageFragment()
        fragment.arguments = bundle
        return fragment
    }
}
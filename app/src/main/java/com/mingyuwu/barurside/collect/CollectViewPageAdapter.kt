package com.mingyuwu.barurside.collect

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class CollectViewPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CollectPageFragment(true)
            1 -> CollectPageFragment(false)
            else -> throw Exception("Unknown position $position")
        }
    }
}
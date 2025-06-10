package com.mingyuwu.barurside.activity

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.util.Util.getString

class ActivityViewPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val type = getString(R.string.activity_tab_type)

        return when (position) {
            0 -> getPageFragment(bundleOf(type to ActivityTypeFilter.RECOMMEND))
            1 -> getPageFragment(bundleOf(type to ActivityTypeFilter.ACTIVITY))
            2 -> getPageFragment(bundleOf(type to ActivityTypeFilter.FOLLOW))
            else -> throw Exception("Unknown position $position")
        }
    }

    private fun getPageFragment(bundle: Bundle): ActivityPageFragment {
        val fragment = ActivityPageFragment()
        fragment.arguments = bundle

        return fragment
    }
}

package com.mingyuwu.barurside.collect

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.util.Util.getString

class CollectViewPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val type = getString(R.string.collect_tab_is_venue)

        return when (position) {
            0 -> getPageFragment(bundleOf(type to true))
            1 -> getPageFragment(bundleOf(type to false))
            else -> throw Exception("Unknown position $position")
        }
    }

    private fun getPageFragment(bundle: Bundle): CollectPageFragment {
        val fragment = CollectPageFragment()
        fragment.arguments = bundle

        return fragment
    }
}

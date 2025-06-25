package com.mingyuwu.barurside.collect

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentCollectBinding

class CollectFragment : Fragment() {
    private lateinit var binding: FragmentCollectBinding
    private var isInit = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCollectBinding.inflate(inflater, container, false)

        // set tab and pager
        binding.viewPagerMain.adapter = CollectViewPageAdapter(this)
        TabLayoutMediator(binding.tabLayoutMain, binding.viewPagerMain) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.collect_tab_venue)
                1 -> tab.text = getString(R.string.collect_tab_drink)
            }
        }.attach()

        // set page listener
        binding.viewPagerMain.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {

                    if (position == 0 && !isInit) {
                        val animator = ObjectAnimator.ofFloat(
                            binding.indicator,
                            "translationX",
                            binding.frame.x,
                            binding.frame.x - binding.frame.left
                        ).setDuration(80)
                        animator.start()
                    } else if (position == 1) {
                        val animator = ObjectAnimator.ofFloat(
                            binding.indicator,
                            "translationX",
                            binding.indicator.x,
                            binding.indicator.x + binding.indicator.width
                        ).setDuration(100)
                        animator.start()
                        isInit = false
                    }

                    super.onPageSelected(position)
                }
            })

        return binding.root
    }
}

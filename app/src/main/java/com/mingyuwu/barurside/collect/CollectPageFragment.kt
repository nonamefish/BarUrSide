package com.mingyuwu.barurside.collect

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.mockdata.CollectData
import com.mingyuwu.barurside.data.mockdata.RatingData
import com.mingyuwu.barurside.databinding.FragmentCollectPageBinding

class CollectPageFragment() : Fragment() {

    private lateinit var binding: FragmentCollectPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_collect_page, container, false
        )

        val isVenue = requireArguments().getBundle("isVenue")

        // data
        val collect = CollectData.collect

        // test collect grid adapter
        val adapter = CollectGridAdapter(
            CollectGridAdapter.OnClickListener {
                Log.d("Ming",it.toString())
            }
        )
        binding.collectList.adapter = adapter
        adapter.submitList(collect.collect)

        Log.d("Ming","Here")

        return binding.root
    }
}
package com.mingyuwu.barurside.rating.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentAllRatingBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.rating.InfoRatingAdapter

class AllRatingFragment : Fragment() {

    private lateinit var binding: FragmentAllRatingBinding
    private val viewModel by viewModels<AllRatingViewModel> {
        getVmFactory(
            AllRatingFragmentArgs.fromBundle(requireArguments()).ratings.toList()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_all_rating, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // set adapter
        val adapter = InfoRatingAdapter()
        binding.recyclerRatingList.adapter = adapter
        viewModel.ratings.observe(
            viewLifecycleOwner, {
                it?.let {
                    adapter.submitList(viewModel.ratings.value)
                }
            }
        )

        return binding.root
    }
}

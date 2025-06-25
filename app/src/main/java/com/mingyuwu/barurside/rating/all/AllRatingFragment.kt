package com.mingyuwu.barurside.rating.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentAllRatingBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.rating.InfoRatingAdapter

class AllRatingFragment : Fragment() {

    private var _binding: FragmentAllRatingBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentAllRatingBinding.inflate(inflater, container, false)

        // set adapter
        val adapter = InfoRatingAdapter()
        binding.rvRatingList.adapter = adapter
        viewModel.ratings.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(viewModel.ratings.value)
            }
        }

        // 星星區塊互動與顯示
        setupStarSection()
        updateStarSection(viewModel.numSelected.value)
        viewModel.numSelected.observe(viewLifecycleOwner) { selected ->
            updateStarSection(selected)
        }

        return binding.root
    }

    private fun setupStarSection() {
        val starLayouts = listOf(
            binding.layoutStar5 to 5,
            binding.layoutStar4 to 4,
            binding.layoutStar3 to 3,
            binding.layoutStar2 to 2,
            binding.layoutStar1 to 1
        )
        for ((layout, star) in starLayouts) {
            layout.setOnClickListener { viewModel.getSpecificStarComment(star) }
        }
    }

    private fun updateStarSection(selected: Int?) {
        val bgNormal = ContextCompat.getDrawable(requireContext(), R.drawable.background_rating_all)
        val bgSelected = ContextCompat.getDrawable(requireContext(), R.drawable.background_rating_all_select)
        val starViews = listOf(
            Triple(binding.layoutStar5, binding.txtStarCount5, 5),
            Triple(binding.layoutStar4, binding.txtStarCount4, 4),
            Triple(binding.layoutStar3, binding.txtStarCount3, 3),
            Triple(binding.layoutStar2, binding.txtStarCount2, 2),
            Triple(binding.layoutStar1, binding.txtStarCount1, 1)
        )
        for ((layout, txt, star) in starViews) {
            layout.background = if (selected == star) bgSelected else bgNormal
            txt.text = getString(R.string.rating_star_count, viewModel.getSpecificStarCount(star))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.mingyuwu.barurside.filter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentFilterBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.editrating.EditRatingFragmentArgs
import com.mingyuwu.barurside.editrating.EditRatingViewModel
import com.mingyuwu.barurside.ext.getVmFactory


class FilterFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFilterBinding

    private val viewModel by viewModels<FilterViewModel> { getVmFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MyBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // set binding and viewModel
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // set confirm button click listener
        binding.btnConfirm.setOnClickListener {
            viewModel.navigateToResult()
        }

        // set chip group selected item
        binding.chipGroupLevel.setOnCheckedChangeListener { chipGroup, id ->
            viewModel.choiceLevel.value = chipGroup.findViewById<Chip>(id).text.length
        }

        // navigate to filter result
        viewModel.navigateToResult.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.MAP_FILTER,
                    null,
                    it
                )
            )
        })

        // Inflate the layout for this fragment
        return binding.root
    }

}
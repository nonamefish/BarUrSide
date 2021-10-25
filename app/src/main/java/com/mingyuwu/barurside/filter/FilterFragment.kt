package com.mingyuwu.barurside.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentFilterBinding
import com.mingyuwu.barurside.discover.Theme


class FilterFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MapFilterDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFilterBinding.inflate(inflater, container, false)

        // set confirm button click listener
        binding.btnConfirm.setOnClickListener {
            findNavController().navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.MAP_FILTER,
                    null,
                    FilterParameter(0,listOf(null),listOf(null),null)
                )
            )
        }

        // Inflate the layout for this fragment
        return binding.root
    }

}
package com.mingyuwu.barurside.filter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
    ): View {

        // set binding and viewModel
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // set confirm button click listener
        binding.btnConfirm.setOnClickListener {

            if (viewModel.checkValue()) { // user have set field
                viewModel.choiceStyle.value = binding.chipGroupStyle.checkedChipIds.map {
                    binding.chipGroupStyle.resources.getResourceEntryName(it).uppercase()
                }
                viewModel.choiceCategory.value = binding.chipGroupCategory.checkedChipIds.map {
                    binding.chipGroupStyle.resources.getResourceEntryName(it).uppercase()
                }
                viewModel.navigateToResult()
            } else {
                showAddUncompleted()  // remind user some filed haven't set
            }
        }

        // set chip group selected item
        binding.chipGroupLevel.setOnCheckedChangeListener { chipGroup, id ->
            viewModel.choiceLevel.value = chipGroup.findViewById<Chip>(id).text.length
        }

        // set chip group selected item
        binding.chipGroupDistance.setOnCheckedChangeListener { chipGroup, id ->
            viewModel.choiceDistance.value = when (chipGroup.findViewById<Chip>(id).text) {
                "500公尺" -> 0.5
                "1公里" -> 1.0
                else -> 2.0
            }
        }

        // navigate to filter result
        viewModel.navigateToResult.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    MainNavigationDirections.navigateToDiscoverDetailFragment(
                        Theme.MAP_FILTER,
                        null,
                        it
                    )
                )
                viewModel.onLeft()
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun showAddUncompleted() {
        // set dialog
        val alertDialog = AlertDialog.Builder(binding.root.context)
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_rating_uncompleted, null)
        val btDialog = mView!!.findViewById<Button>(R.id.button_confirm)
        val txtDialog = mView.findViewById<TextView>(R.id.dialog_content)
        alertDialog.setView(mView)
        val dialog = alertDialog.create()

        // set dialog content text and button click listener
        txtDialog.text = "價格範圍為必填項目"
        btDialog.setOnClickListener { dialog.dismiss() }
        dialog.show()

        // set parameter
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}
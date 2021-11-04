package com.mingyuwu.barurside.activity.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.addactivity.AddActivityViewModel
import com.mingyuwu.barurside.data.mockdata.UserData
import com.mingyuwu.barurside.databinding.DialogActivityDetailBinding
import com.mingyuwu.barurside.ext.getVmFactory

class ActivityDetailDialog: DialogFragment() {

    private val viewModel by viewModels<AddActivityViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DialogActivityDetailBinding.inflate(layoutInflater)
        val activity = ActivityDetailDialogArgs.fromBundle(requireArguments()).activity
        binding.activity = activity

        // set mock data
        binding.user = UserData.user.user[0]

        // book button on click listener
        binding.btnBookActivity.setOnClickListener {
            findNavController().popBackStack()
        }

        // Set transparent background and no title
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        }

        return binding.root
    }
}
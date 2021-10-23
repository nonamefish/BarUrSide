package com.mingyuwu.barurside.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.data.mockdata.UserData
import com.mingyuwu.barurside.databinding.DialogActivityDetailBinding

class ActivityDetailDialog: DialogFragment() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NO_FRAME, R.style.ActivityDetailDialog)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DialogActivityDetailBinding.inflate(layoutInflater)
        binding.activity = ActivityDetailDialogArgs.fromBundle(requireArguments()).activity

        // set mock data
        binding.user = UserData.user.user[0]
//        bindind.

        // book button on click listener
        binding.btnBookActivity.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}
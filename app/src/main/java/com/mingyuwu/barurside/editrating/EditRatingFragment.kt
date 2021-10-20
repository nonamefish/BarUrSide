package com.mingyuwu.barurside.editrating

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentEditRatingBinding

class EditRatingFragment : Fragment() {

    private lateinit var binding: FragmentEditRatingBinding
    private lateinit var viewModel : EditRatingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_rating, container, false
        )
        viewModel = ViewModelProvider(this).get(EditRatingViewModel::class.java)
        binding.lifecycleOwner = this

        // set adapter
        val adapter = EditRatingAdapter(true,viewModel)
        binding.venusRtgScoreList.adapter = adapter
        adapter.submitList(listOf("1"))



        // Inflate the layout for this fragment
        return binding.root
    }

}
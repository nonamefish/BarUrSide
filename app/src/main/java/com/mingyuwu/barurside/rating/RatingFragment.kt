package com.mingyuwu.barurside.rating

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentRatingBinding

class RatingFragment : Fragment() {

    private lateinit var binding: FragmentRatingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_rating, container, false
        )

        //


        // Inflate the layout for this fragment
        return binding.root
    }

}
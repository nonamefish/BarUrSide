package com.mingyuwu.barurside.addactivity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentActivityPageBinding
import com.mingyuwu.barurside.databinding.FragmentAddActivityBinding
import com.mingyuwu.barurside.editrating.EditRatingViewModel
import com.mingyuwu.barurside.ext.getVmFactory
import java.text.SimpleDateFormat
import java.util.*

class AddActivityFragment : Fragment() {

    private val calender = Calendar.getInstance()
    private lateinit var binding: FragmentAddActivityBinding
    private val viewModel by viewModels<AddActivityViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_activity, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // activity start time
        binding.addActivityStart.setOnClickListener {
            datePicker(viewModel.startTime)
        }

        // activity end time
        binding.addActivityEnd.setOnClickListener {
            datePicker(viewModel.endTime)
        }

        // confirm button
        binding.btnAddActovityConfirm.setOnClickListener {
            findNavController().navigate(MainNavigationDirections.navigateToActivityFragment())

        }

        // cancel button
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }


        return binding.root
    }

    private fun datePicker(datetime: MutableLiveData<String>) {
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            timePicker(datetime)
            datetime.value=""
            calender.set(year, month, day)
            format("yyyy/MM/dd", datetime)
        }

        DatePickerDialog(
            binding.root.context,
            R.style.ThemeOverlay_App_DatePicker,
            dateListener,
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun timePicker(datetime: MutableLiveData<String>) {
        val timeListener = TimePickerDialog.OnTimeSetListener { _, hour, min ->
            calender.set(Calendar.HOUR_OF_DAY, hour)
            calender.set(Calendar.MINUTE, min)
            format(" a HH:mm", datetime)
        }

        TimePickerDialog(
            binding.root.context,
            R.style.ThemeOverlay_App_DatePicker,
            timeListener,
            calender.get(Calendar.HOUR_OF_DAY),
            calender.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun format(format: String, datetime: MutableLiveData<String>) {
        val time = SimpleDateFormat(format, Locale.TAIWAN)
        datetime.value = "${datetime.value} ${time.format(calender.time)}"
    }
}
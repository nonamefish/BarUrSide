package com.mingyuwu.barurside.addactivity

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.databinding.FragmentAddActivityBinding
import com.mingyuwu.barurside.ext.getVmFactory
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.libraries.places.api.Places
import com.mingyuwu.barurside.R

import com.google.android.libraries.places.widget.Autocomplete

import android.content.Intent
import android.util.Log
import com.google.android.gms.common.api.Status

import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode


const val AUTOCOMPLETE_REQUEST_CODE = 101

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

        // Initialize place api
        Places.initialize(binding.root.context, getString(R.string.google_maps_key))

        // return after the user has made a selection.
        val field = listOf(Place.Field.ID, Place.Field.ADDRESS)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
            .setCountries(listOf("TW"))
            .build(binding.root.context)


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

        // address edit text click listener
        binding.addActivityAddress.setOnClickListener{ // Set the fields to specify which types of place data to

            //start activity result
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //When success initialize place
                val place = Autocomplete.getPlaceFromIntent(data)

                //set address on edittext
                binding.addActivityAddress.text = place.address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status: Status = Autocomplete.getStatusFromIntent(data)
                Log.d("Ming", status.statusMessage!!);
                Log.d("Ming","RESULT_ERROR")
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.d("Ming","RESULT_CANCELED")
            }
        }
    }
}
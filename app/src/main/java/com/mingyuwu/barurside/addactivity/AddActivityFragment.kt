package com.mingyuwu.barurside.addactivity

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentAddActivityBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.ext.showToast
import com.mingyuwu.barurside.util.Util
import com.mingyuwu.barurside.util.Util.convertStringToTimestamp
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog as DatePickerDialog1

class AddActivityFragment : Fragment() {

    private val calender = Calendar.getInstance()
    private lateinit var binding: FragmentAddActivityBinding
    private val startForMapAutoComplete = registerStartForMapAutoComplete()
    private val viewModel by viewModels<AddActivityViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_activity, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Initialize place api
        Places.initialize(requireContext(), getString(R.string.google_maps_key))

        // return after the user has made a selection.
        val field = listOf(Place.Field.ID, Place.Field.ADDRESS)

        // Start the autocomplete intent.
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
            .setCountries(listOf("TW"))
            .build(requireContext())

        // address edit text click listener
        binding.addActivityAddress.setOnClickListener {
            // Set the fields to specify which types of place data to
            startForMapAutoComplete.launch(intent)
        }

        // call datePicker for set activity start time
        binding.addActivityStart.setOnClickListener {
            datePicker(viewModel.startTime)
        }

        // call datePicker for set activity end time
        binding.addActivityEnd.setOnClickListener {
            datePicker(viewModel.endTime)
        }

        // set post rating button click listener
        var alertDialog: AlertDialog? = null

        // confirm button
        binding.btnAddActovityConfirm.setOnClickListener {
            if (viewModel.checkValueCompleted()) {
                if (viewModel.checkTimeRange()) {
                    viewModel.postActivity()
                    alertDialog = postRatingDialog(AlertDialog.Builder(binding.root.context))
                } else {
                    showRatingUncompleted(
                        Util.getString(R.string.date_range_error_title),
                        Util.getString(R.string.date_range_error_content)
                    )
                }
            } else {
                showRatingUncompleted(
                    Util.getString(R.string.activity_uncompleted_title),
                    Util.getString(R.string.activity_uncompleted_content)
                )
            }
        }

        // after post activity then navigate to activity fragment
        viewModel.navigateToDetail.observe(
            viewLifecycleOwner, {
                it?.let {
                    alertDialog!!.dismiss()
                    findNavController().navigate(
                        MainNavigationDirections.navigateToActivityFragment()
                    )
                    viewModel.onLeft()
                }
            }
        )

        // cancel button
        binding.btnCancel.setOnClickListener {
            findNavController().navigate(MainNavigationDirections.navigateToActivityFragment())
        }

        return binding.root
    }

    private fun datePicker(datetime: MutableLiveData<String>) {

        val dateListener = DatePickerDialog1.OnDateSetListener { _, year, month, day ->
            timePicker(datetime)
            datetime.value = ""
            calender.set(year, month, day)
            format(Util.getString(R.string.date_format), datetime)
        }

        val dialog = DatePickerDialog1(
            binding.root.context,
            R.style.ThemeOverlay_App_DatePicker,
            dateListener,
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )

        // DatePickerDialog Foolproof
        dialog.datePicker.minDate = if (datetime == viewModel.startTime) {
            Calendar.getInstance().timeInMillis
        } else {
            viewModel.startTime.value?.let {
                convertStringToTimestamp(it).time
            } ?: Calendar.getInstance().timeInMillis
        }

        dialog.show()
    }

    private fun timePicker(datetime: MutableLiveData<String>) {

        val timeListener = TimePickerDialog.OnTimeSetListener { _, hour, min ->
            calender.set(Calendar.HOUR_OF_DAY, hour)
            calender.set(Calendar.MINUTE, min)
            format(Util.getString(R.string.time_format), datetime)
        }

        val dialog = TimePickerDialog(
            binding.root.context,
            com.google.android.material.R.style.ThemeOverlay_MaterialComponents_TimePicker,
            timeListener,
            calender.get(Calendar.HOUR_OF_DAY),
            calender.get(Calendar.MINUTE),
            false
        )

        dialog.show()
    }

    private fun format(format: String, datetime: MutableLiveData<String>) {
        val time = SimpleDateFormat(format, Locale.TAIWAN)
        datetime.value = "${datetime.value} ${time.format(calender.time)}"
    }

    private fun registerStartForMapAutoComplete(): ActivityResultLauncher<Intent> {

        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            if (data != null) {
                when (resultCode) {
                    RESULT_OK -> {
                        // When success initialize place
                        val place = Autocomplete.getPlaceFromIntent(data)

                        // set address on edittext
                        binding.addActivityAddress.text = place.address
                    }
                    AutocompleteActivity.RESULT_ERROR -> {
                        val status: Status = Autocomplete.getStatusFromIntent(data)
                        showToast("message: ${status.statusMessage}")
                    }
                }
            }
        }
    }

    private fun showRatingUncompleted(title: String, content: String) {
        // set alert dialog view
        val alertDialog = AlertDialog.Builder(binding.root.context)
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_rating_uncompleted, null)
        alertDialog.setView(mView)
        val dialog = alertDialog.create()

        mView.let{
            // set dialog content
            val txtDialog = it.findViewById<TextView>(R.id.dialog_content)
            val titleDialog = it.findViewById<TextView>(R.id.dialog_title)
            titleDialog.text = title
            txtDialog.text = content

            // set button click listener
            val btDialog = it.findViewById<Button>(R.id.button_confirm)
            btDialog.setOnClickListener { dialog.dismiss() }
        }

        dialog.show()

        // set border as transparent
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun postRatingDialog(postDialog: AlertDialog.Builder): AlertDialog {
        // set dialog
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_post_rating, null)
        val txtDialog = mView.findViewById<TextView>(R.id.dialog_content)
        postDialog.setView(mView)
        val dialog = postDialog.create()

        // set dialog content text
        txtDialog.text = Util.getString(R.string.adding_activity)

        // set parameter
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }
}

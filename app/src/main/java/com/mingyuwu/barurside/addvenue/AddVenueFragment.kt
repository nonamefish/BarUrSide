package com.mingyuwu.barurside.addvenue

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.Constants.AUTOCOMPLETE_REQUEST_CODE
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentAddVenueBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.util.Style
import com.mingyuwu.barurside.util.Util
import com.permissionx.guolindev.PermissionX
import java.text.SimpleDateFormat
import java.util.*

class AddVenueFragment : Fragment() {

    private lateinit var binding: FragmentAddVenueBinding
    private val calender = Calendar.getInstance()
    private var photoPermissionGranted = false
    private val viewModel by viewModels<AddVenueViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_venue, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // set post rating button click listener
        var alertDialog: AlertDialog? = null

        // confirm button
        binding.btnAddActovityConfirm.setOnClickListener {
            if (viewModel.checkValue()) {
                viewModel.uploadPhoto()
                alertDialog = postRatingDialog(AlertDialog.Builder(binding.root.context))
            } else {
                showAddUncompleted()
            }
        }

        // Initialize place api
        Places.initialize(requireContext(), getString(R.string.google_maps_key))

        // return after the user has made a selection.
        val field = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
            .setCountries(listOf("TW"))
            .build(requireContext())

        // address edit text click listener
        binding.txtVenueAddress.setOnClickListener {
            //start activity result
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        // set spinner style and adapter
        val adapterStyle = ArrayAdapter.createFromResource(
            binding.root.context,
            R.array.venue_style,
            R.layout.spinner_search_type
        )

        // set spinner style adapter
        binding.spinnerVenueStyle.adapter = adapterStyle

        binding.spinnerVenueStyle.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (position != 0) {
                        val selected = parent?.getItemAtPosition(position).toString()
                        viewModel.style.value = Style.values().find { it.chinese == selected }?.name
                    } else {
                        viewModel.style.value = null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // set spinner level adapter
        val adapterLevel = ArrayAdapter.createFromResource(
            binding.root.context,
            R.array.venue_level,
            R.layout.spinner_search_type
        )

        binding.spinnerVenueLevel.adapter = adapterLevel
        binding.spinnerVenueLevel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (position != 0) {
                        viewModel.level.value = position
                    } else {
                        viewModel.level.value = null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // activity start time
        binding.venueServiceTimeStart.setOnClickListener {
            timePicker(viewModel.startTime)
        }

        // activity end time
        binding.venueServiceTimeClose.setOnClickListener {
            timePicker(viewModel.closeTime)
        }

        // cancel button
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        // add photo
        binding.txtVenuePhoto.setOnClickListener {
            getPhotoPermission()
        }

        // after post activity then navigate to activity fragment
        viewModel.leave.observe(viewLifecycleOwner, { leave ->
            leave?.let {
                alertDialog?.dismiss()
                findNavController().navigate(MainNavigationDirections.navigateToDiscoverFragment())
                viewModel.onLeft()
            }
        })

        return binding.root
    }

    private fun timePicker(datetime: MutableLiveData<String>) {

        val timeListener = TimePickerDialog.OnTimeSetListener { _, hour, min ->
            calender.set(Calendar.HOUR_OF_DAY, hour)
            calender.set(Calendar.MINUTE, min)
            format(Util.getString(R.string.service_format), datetime)
        }

        val timePicker = TimePickerDialog(
            binding.root.context,
            R.style.ThemeOverlay_MaterialComponents_TimePicker,
            timeListener,
            calender.get(Calendar.HOUR_OF_DAY),
            calender.get(Calendar.MINUTE),
            false
        )

        timePicker.show()
    }

    private fun format(format: String, datetime: MutableLiveData<String>) {
        val time = SimpleDateFormat(format, Locale.TAIWAN)
        datetime.value = time.format(calender.time)
    }

    private fun getPhotoPermission() {
        PermissionX.init(activity)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    Util.getString(R.string.permission_camera),
                    Util.getString(R.string.permission_confirm),
                    Util.getString(R.string.permission_reject)
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    Util.getString(R.string.permission_camera),
                    Util.getString(R.string.permission_confirm),
                    Util.getString(R.string.permission_reject)
                )
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    photoPermissionGranted = true
                    chooseImage(binding.root.context)
                } else {
                    Toast.makeText(
                        binding.root.context,
                        getString(R.string.permission_reject_toast, deniedList),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun chooseImage(context: Context) {

        // create a menuOption Array
        val optionsMenu = arrayOf<CharSequence>(
            Util.getString(R.string.from_gallery),
            Util.getString(R.string.exit),
        )

        // create a dialog for showing the optionsMenu
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        // set the items in builder
        builder.setItems(
            optionsMenu
        ) { dialogInterface, i ->
            if (optionsMenu[i] == Util.getString(R.string.from_gallery)) {
                // choose from  external storage
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, 1)
            } else if (optionsMenu[i] == Util.getString(R.string.exit)) {
                dialogInterface.dismiss()
            }
        }

        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data

                    if (selectedImage != null) {
                        val inputStream =
                            context?.contentResolver?.openInputStream(selectedImage)
                        val bitMap = BitmapFactory.decodeStream(inputStream)
                        val fileName = "${Util.randomName(20)}.jpg"

                        // resize image and save into another img
                        bitMap?.let {
                            val resizeImg = Util.getResizedBitmap(bitMap, 1000)
                            val pathSave = Util.saveBitmap(resizeImg, fileName)
                            addImageToRecyclerView(resizeImg, pathSave)
                        }
                    }
                }
                AUTOCOMPLETE_REQUEST_CODE -> {
                    if (resultCode == Activity.RESULT_OK) {

                        data?.let {
                            //When success initialize place
                            val place = Autocomplete.getPlaceFromIntent(it)

                            //set address on edittext
                            viewModel.address.value = place.address
                            viewModel.latitude.value = place.latLng?.latitude
                            viewModel.longtitude.value = place.latLng?.longitude
                        }


                    } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                        data?.let {
                            val status: Status = Autocomplete.getStatusFromIntent(data)
                            Toast.makeText(
                                BarUrSideApplication.appContext,
                                "message: ${status.statusMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun addImageToRecyclerView(bitmap: Bitmap, url: String) {
        viewModel.addUploadImg(bitmap, url)
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
        txtDialog.text = Util.getString(R.string.venue_uncompleted_context)
        btDialog.setOnClickListener { dialog.dismiss() }
        dialog.show()

        // set parameter
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
        txtDialog.text = Util.getString(R.string.adding_venue)

        // set parameter
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

}
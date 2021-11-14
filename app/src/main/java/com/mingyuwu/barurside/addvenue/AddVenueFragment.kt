package com.mingyuwu.barurside.addvenue

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.R
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.Observer
import android.widget.*
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.addactivity.AUTOCOMPLETE_REQUEST_CODE
import com.mingyuwu.barurside.adddrink.AddDrinkViewModel
import com.mingyuwu.barurside.databinding.FragmentAddVenueBinding
import com.permissionx.guolindev.PermissionX
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


const val REQUEST_ID_MULTIPLE_PERMISSIONS = 101
private var photoPermissionGranted = false

class AddVenueFragment : Fragment() {

    private val calender = Calendar.getInstance()
    private lateinit var binding: FragmentAddVenueBinding
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
        binding.txtVenueAddress.setOnClickListener { // Set the fields to specify which types of place data to
            //start activity result
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        //set spinner style and adapter
        val adapterStyle = ArrayAdapter.createFromResource(
            binding.root.context,
            R.array.venue_style,
            R.layout.spinner_search_type
        )

        binding.spinnerVenueStyle.adapter = adapterStyle
        binding.spinnerVenueStyle.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (position != 0) {
                        viewModel.style.value = parent?.getItemAtPosition(position).toString()
                    } else {
                        viewModel.style.value = null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        //set spinner level and adapter
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
        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                alertDialog!!.dismiss()
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
            format("HH:mm", datetime)
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
        Log.d("Ming", datetime.value.toString())
    }


    private fun getPhotoPermission() {
        PermissionX.init(activity)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "請開通相片權限，以上傳照片",
                    "確定",
                    "忍痛拒絕"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "請開通相片權限，以上傳照片",
                    "確定",
                    "忍痛拒絕"
                )
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    photoPermissionGranted = true
                    chooseImage(binding.root.context)
//                    Toast.makeText(binding.root.context, "All permissions are granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "These permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun chooseImage(context: Context) {
        val optionsMenu = arrayOf<CharSequence>("從照片選擇", "離開") // create a menuOption Array
        // create a dialog for showing the optionsMenu
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        // set the items in builder
        builder.setItems(
            optionsMenu
        ) { dialogInterface, i ->
            if (optionsMenu[i] == "從照片選擇") {
                // choose from  external storage
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, 1)
            } else if (optionsMenu[i] == "Exit") {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Ming", "onActivityResult")
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data
                    val filePathColumn = arrayOf<String>(MediaStore.Images.Media.DATA)

                    if (selectedImage != null) {
                        val cursor: Cursor? = activity?.contentResolver?.query(
                            selectedImage,
                            filePathColumn,
                            null,
                            null,
                            null
                        )
                        if (cursor != null) {

                            val columnIndex =
                                cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                            cursor.moveToFirst()
                            val picturePath: String = cursor.getString(columnIndex)
                            val img = BitmapFactory.decodeFile(picturePath)

                            // resize image and save into another img
                            val pathNew =
                                picturePath.split(".")[0] + "_1." + picturePath.split(".")[1]
                            saveBitmap(getResizedBitmap(img, 1000)!!, pathNew)
                            addImageToRecyclerView(getResizedBitmap(img, 1000), pathNew)
                            cursor.close()
                        }
                    }
                }
                AUTOCOMPLETE_REQUEST_CODE->{
                    if (resultCode == Activity.RESULT_OK) {
                        //When success initialize place
                        val place = Autocomplete.getPlaceFromIntent(data)

                        //set address on edittext
                        viewModel.address.value = place.address
                        viewModel.latitude.value = place.latLng.latitude
                        viewModel.longtitude.value = place.latLng.longitude
                        Log.d("Ming", viewModel.address!!.value.toString())
                    } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                        val status: Status = Autocomplete.getStatusFromIntent(data)
                        Toast.makeText(
                            BarUrSideApplication.appContext,
                            "message: ${status.statusMessage!!}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("Ming", status.statusMessage!!)
                    }
                }
            }
        }
    }

    private fun addImageToRecyclerView(bitmap: Bitmap, url: String) {
        viewModel.addUploadImg(bitmap, url)
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
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
        txtDialog.text = "酒名、類別及價格為必填項目"
        btDialog.setOnClickListener { dialog.dismiss() }
        dialog.show()

        // set parameter
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun saveBitmap(bitmap: Bitmap, path: String) {
        val fOut: FileOutputStream
        try {
            fOut = FileOutputStream(path)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            try {
                fOut.flush()
                fOut.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun postRatingDialog(postDialog: AlertDialog.Builder): AlertDialog {
        // set dialog
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_post_rating, null)
        val txtDialog = mView.findViewById<TextView>(R.id.dialog_content)
        postDialog.setView(mView)
        val dialog = postDialog.create()

        // set dialog content text
        txtDialog.text = "店家資訊新增中"

        // set parameter
        dialog.show()
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}
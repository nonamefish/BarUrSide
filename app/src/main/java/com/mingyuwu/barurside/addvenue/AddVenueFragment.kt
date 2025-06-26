package com.mingyuwu.barurside.addvenue

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
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
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
import com.mingyuwu.barurside.databinding.FragmentAddVenueBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.ext.isPermissionGranted
import com.mingyuwu.barurside.ext.requestPermission
import com.mingyuwu.barurside.ext.showToast
import com.mingyuwu.barurside.util.AppPermission
import com.mingyuwu.barurside.util.Style
import com.mingyuwu.barurside.util.Util
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.core.graphics.drawable.toDrawable

class AddVenueFragment : Fragment() {

    private var _binding: FragmentAddVenueBinding? = null
    private val binding get() = _binding!!
    private val startForGallery = registerStartForGallery()
    private val startForMapAutoComplete = registerStartForMapAutoComplete()
    private val calender = Calendar.getInstance()
    private val viewModel by viewModels<AddVenueViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddVenueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 確保 Places API 已初始化
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
        }
        var alertDialog: AlertDialog? = null

        // name
        viewModel.name.observe(viewLifecycleOwner) { name ->
            if (binding.editTxtVenueName.text.toString() != name) {
                binding.editTxtVenueName.setText(name ?: "")
            }
        }
        binding.editTxtVenueName.doAfterTextChanged {
            viewModel.name.value = it?.toString()
        }

        // address
        viewModel.address.observe(viewLifecycleOwner) { address ->
            binding.txtVenueAddress.text = address ?: ""
        }
        binding.txtVenueAddress.setOnClickListener {
            // 啟動地圖自動補全
            val field = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent = Autocomplete
                .IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                .setCountries(listOf("TW"))
                .build(requireContext())
            startForMapAutoComplete.launch(intent)
        }

        // phone
        viewModel.phone.observe(viewLifecycleOwner) { phone ->
            if (binding.editTxtVenuePhone.text.toString() != phone) {
                binding.editTxtVenuePhone.setText(phone ?: "")
            }
        }
        binding.editTxtVenuePhone.doAfterTextChanged {
            viewModel.phone.value = it?.toString()
        }

        // web
        viewModel.web.observe(viewLifecycleOwner) { web ->
            if (binding.editTxtVenueWeb.text.toString() != web) {
                binding.editTxtVenueWeb.setText(web ?: "")
            }
        }
        binding.editTxtVenueWeb.doAfterTextChanged {
            viewModel.web.value = it?.toString()
        }

        // spinner style
        val adapterStyle = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.venue_style,
            R.layout.spinner_search_type
        )
        binding.spinnerVenueStyle.adapter = adapterStyle
        binding.spinnerVenueStyle.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {
                        val selected = parent?.getItemAtPosition(position).toString()
                        viewModel.style.value = Style.entries.find { it.chinese == selected }?.name
                    } else {
                        viewModel.style.value = null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        // spinner level
        val adapterLevel = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.venue_level,
            R.layout.spinner_search_type
        )
        binding.spinnerVenueLevel.adapter = adapterLevel
        binding.spinnerVenueLevel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.level.value = if (position != 0) position else null
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // service time
        viewModel.startTime.observe(viewLifecycleOwner) { start ->
            binding.txtVenueServiceTimeStart.text = start ?: ""
        }
        binding.txtVenueServiceTimeStart.setOnClickListener {
            timePicker(viewModel.startTime)
        }
        viewModel.closeTime.observe(viewLifecycleOwner) { close ->
            binding.txtVenueServiceTimeClose.text = close ?: ""
        }
        binding.txtVenueServiceTimeClose.setOnClickListener {
            timePicker(viewModel.closeTime)
        }

        // image
        viewModel.image.observe(viewLifecycleOwner) { bitmap ->
            bitmap?.let {
                binding.imgVenuePreview.setImageBitmap(it)
            }
        }

        // confirm button
        binding.btnAddVenueConfirm.setOnClickListener {
            if (viewModel.checkValue()) {
                viewModel.uploadPhoto()
                alertDialog = postRatingDialog(AlertDialog.Builder(binding.root.context))
            } else {
                showAddUncompleted()
            }
        }
        // cancel button
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        // add venue photo onclick
        binding.txtVenuePhoto.setOnClickListener {
            context?.let { chooseImage(it) }
        }
        // after post activity then navigate to activity fragment
        viewModel.leave.observe(viewLifecycleOwner) { leave ->
            leave?.let {
                alertDialog?.dismiss()
                findNavController().navigate(MainNavigationDirections.navigateToDiscoverFragment())
                viewModel.onLeft()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun timePicker(datetime: MutableLiveData<String>) {

        val timeListener = TimePickerDialog.OnTimeSetListener { _, hour, min ->
            calender.set(Calendar.HOUR_OF_DAY, hour)
            calender.set(Calendar.MINUTE, min)
            format(Util.getString(R.string.service_format), datetime)
        }

        val timePicker = TimePickerDialog(
            binding.root.context,
            com.google.android.material.R.style.ThemeOverlay_MaterialComponents_TimePicker,
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

    private fun chooseImage(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startForGallery.launch(Intent(MediaStore.ACTION_PICK_IMAGES))
            return
        }

        if (!isPermissionGranted(AppPermission.ReadExternalStorage)) {
            requestPermission(AppPermission.ReadExternalStorage)
            chooseImage(context)
            return
        }

        val optionsMenu = arrayOf(
            Util.getString(R.string.from_gallery),
            Util.getString(R.string.exit)
        )
        AlertDialog.Builder(context)
            .setItems(optionsMenu) { dialog, i ->
                when (optionsMenu[i]) {
                    Util.getString(R.string.from_gallery) -> {
                        val pickPhoto = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        startForGallery.launch(pickPhoto)
                    }
                    Util.getString(R.string.exit) -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun registerStartForGallery(): ActivityResultLauncher<Intent> {

        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK && data != null) {

                val selectedImage: Uri? = data.data

                if (selectedImage != null) {
                    val inputStream = context?.contentResolver?.openInputStream(selectedImage)
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
        }
    }

    private fun registerStartForMapAutoComplete(): ActivityResultLauncher<Intent> {

        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {

                data?.let {

                    // When success initialize place
                    val place = Autocomplete.getPlaceFromIntent(it)

                    // set address on edittext
                    viewModel.address.value = place.address
                    viewModel.latitude.value = place.latLng?.latitude
                    viewModel.longitude.value = place.latLng?.longitude
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                data?.let {

                    val status: Status = Autocomplete.getStatusFromIntent(data)
                    showToast("message: ${status.statusMessage}")
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
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
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
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        return dialog
    }

}

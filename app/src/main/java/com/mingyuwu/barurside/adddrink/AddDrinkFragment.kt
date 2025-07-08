package com.mingyuwu.barurside.adddrink

import android.app.Activity
import android.app.AlertDialog
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
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentAddDrinkBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.ext.isPermissionGranted
import com.mingyuwu.barurside.ext.requestPermission
import com.mingyuwu.barurside.util.AppPermission
import com.mingyuwu.barurside.util.Category
import com.mingyuwu.barurside.util.Util
import com.mingyuwu.barurside.util.Util.getResizedBitmap
import com.mingyuwu.barurside.util.Util.randomName
import com.mingyuwu.barurside.util.Util.saveBitmap

class AddDrinkFragment : Fragment() {

    private var _binding: FragmentAddDrinkBinding? = null
    private val binding get() = _binding!!
    private val startForGallery = registerStartForGallery()
    private val viewModel by viewModels<AddDrinkViewModel> {
        getVmFactory(
            AddDrinkFragmentArgs.fromBundle(requireArguments()).id
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddDrinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = AddDrinkFragmentArgs.fromBundle(requireArguments()).id
        var alertDialog: AlertDialog? = null

        // venue name
        viewModel.venue.observe(viewLifecycleOwner) { venue ->
            binding.txtVenueName.text = venue?.name ?: ""
        }

        // editText: name
        viewModel.name.observe(viewLifecycleOwner) { name ->
            if (binding.editTxtDrinkName.text.toString() != name) {
                binding.editTxtDrinkName.setText(name ?: "")
            }
        }
        binding.editTxtDrinkName.doAfterTextChanged {
            viewModel.name.value = it?.toString()
        }

        // editText: price
        viewModel.price.observe(viewLifecycleOwner) { price ->
            if (binding.editTxtDrinkPrice.text.toString() != price) {
                binding.editTxtDrinkPrice.setText(price ?: "")
            }
        }
        binding.editTxtDrinkPrice.doAfterTextChanged {
            viewModel.price.value = it?.toString()
        }

        // editText: description
        viewModel.description.observe(viewLifecycleOwner) { desc ->
            if (binding.editTxtDrinkDesc.text.toString() != desc) {
                binding.editTxtDrinkDesc.setText(desc ?: "")
            }
        }
        binding.editTxtDrinkDesc.doAfterTextChanged {
            viewModel.description.value = it?.toString()
        }

        // spinner type
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.drink_type,
            R.layout.spinner_search_type
        )
        binding.spinnerDrinkType.adapter = adapter
        binding.spinnerDrinkType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    if (position != 0) {
                        val selected = parent?.getItemAtPosition(position).toString()
                        viewModel.type.value =
                            Category.values().find { it.chinese == selected }?.name
                    } else {
                        viewModel.type.value = null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        // spinner value observe
        viewModel.type.observe(viewLifecycleOwner) { type ->
            val idx = Category.values().indexOfFirst { it.name == type }
            if (idx >= 0 && binding.spinnerDrinkType.selectedItemPosition != idx) {
                binding.spinnerDrinkType.setSelection(idx)
            }
        }

        // image
        viewModel.image.observe(viewLifecycleOwner) { bitmap ->
            if (bitmap != null) {
                binding.imgDrinkPreview.setImageBitmap(bitmap)
            } else {
                binding.imgDrinkPreview.setImageResource(R.drawable.image_placeholder)
            }
        }

        // confirm button
        binding.btnAddDrinkConfirm.setOnClickListener {
            if (viewModel.checkValue()) {
                viewModel.uploadPhoto()
                alertDialog = postRatingDialog(AlertDialog.Builder(binding.root.context))
            } else {
                showDrinkUncompleted()
            }
        }
        // cancel button
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        // add drink photo onclick
        binding.txtDrinkPhoto.setOnClickListener {
            launchImagePicker(context ?: return@setOnClickListener)
        }
        // after post activity then navigate to activity fragment
        viewModel.leave.observe(viewLifecycleOwner) {
            it?.let {
                alertDialog?.dismiss()
                findNavController().navigate(
                    MainNavigationDirections.navigateToVenueFragment(id)
                )
                viewModel.onLeft()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchImagePicker(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startForGallery.launch(Intent(MediaStore.ACTION_PICK_IMAGES))
            return
        }
        if (!isPermissionGranted(AppPermission.ReadExternalStorage)) {
            requestPermission(AppPermission.ReadExternalStorage)
            launchImagePicker(context)
            return
        }
        showImageSourceDialog(context)
    }

    private fun showImageSourceDialog(context: Context) {
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
                    val fileName = "${randomName(20)}.jpg"

                    // resize image and save into another img
                    bitMap?.let {
                        val resizeImg = getResizedBitmap(bitMap, 1000)
                        val pathSave = saveBitmap(resizeImg, fileName)
                        addImageToRecyclerView(resizeImg, pathSave)
                    }
                }
            }
        }
    }

    private fun addImageToRecyclerView(bitmap: Bitmap, url: String) {
        viewModel.addUploadImg(bitmap, url)
    }

    private fun showDrinkUncompleted() {

        // set dialog
        val alertDialog = AlertDialog.Builder(binding.root.context)
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_rating_uncompleted, null)
        val btDialog = mView!!.findViewById<Button>(R.id.button_confirm)
        val txtDialog = mView.findViewById<TextView>(R.id.dialog_content)
        alertDialog.setView(mView)
        val dialog = alertDialog.create()

        // set dialog content text and button click listener
        txtDialog.text = Util.getString(R.string.drink_uncompleted_context)
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
        txtDialog.text = getString(R.string.adding_drink)

        // set parameter
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

}

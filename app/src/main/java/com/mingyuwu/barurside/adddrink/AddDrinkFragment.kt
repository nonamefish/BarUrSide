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
import androidx.databinding.DataBindingUtil
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

    private lateinit var binding: FragmentAddDrinkBinding
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

        val id = AddDrinkFragmentArgs.fromBundle(requireArguments()).id

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_drink, container, false
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
                showDrinkUncompleted()
            }
        }

        // set spinner type and adapter
        val adapter = ArrayAdapter.createFromResource(
            binding.root.context,
            R.array.drink_type,
            R.layout.spinner_search_type
        )

        binding.spinnerObjectType.adapter = adapter

        binding.spinnerObjectType.onItemSelectedListener =
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

        // cancel post rating button
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        // add drink photo onclick listener
        binding.txtObjectPhoto.setOnClickListener {
            this.context?.let { context -> chooseImage(context) }
        }

        // after post activity then navigate to activity fragment
        viewModel.leave.observe(
            viewLifecycleOwner, {
                it?.let {
                    alertDialog!!.dismiss()
                    findNavController().navigate(
                        MainNavigationDirections.navigateToVenueFragment(id)
                    )
                    viewModel.onLeft()
                }
            }
        )

        return binding.root
    }

    private fun chooseImage(context: Context) {

        if (isPermissionGranted(AppPermission.ReadExternalStorage)) {
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
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )

                    startForGallery.launch(pickPhoto)
                } else if (optionsMenu[i] == Util.getString(R.string.exit)) {
                    dialogInterface.dismiss()
                }
            }

            builder.show()
        } else {

            requestPermission(AppPermission.ReadExternalStorage)

            chooseImage(context)
        }

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

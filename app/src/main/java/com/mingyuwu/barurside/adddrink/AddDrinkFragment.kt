package com.mingyuwu.barurside.adddrink

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import com.mingyuwu.barurside.databinding.FragmentAddDrinkBinding
import com.mingyuwu.barurside.util.Category
import com.mingyuwu.barurside.util.Util
import com.mingyuwu.barurside.util.Util.getResizedBitmap
import com.mingyuwu.barurside.util.Util.randomName
import com.permissionx.guolindev.PermissionX
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


const val REQUEST_ID_MULTIPLE_PERMISSIONS = 101
private var photoPermissionGranted = false

class AddObjectFragment : Fragment() {

    private lateinit var binding: FragmentAddDrinkBinding
    private val viewModel by viewModels<AddDrinkViewModel> {
        getVmFactory(
            AddObjectFragmentArgs.fromBundle(requireArguments()).id
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val id = AddObjectFragmentArgs.fromBundle(requireArguments()).id

        // Inflate the layout for this fragment
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
                showAddUncompleted()
            }
        }

        //set spinner type and adapter
        val adapter = ArrayAdapter.createFromResource(
            binding.root.context,
            R.array.drink_type,
            R.layout.spinner_search_type
        )

        binding.spinnerObjectType.adapter = adapter
        binding.spinnerObjectType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (position != 0) {
                        val selected = parent?.getItemAtPosition(position).toString()
                        viewModel.type.value = Category.values().find { it.chinese == selected }?.name
                    }else{
                        viewModel.type.value = null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        // cancel button
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        // add photo
        binding.txtObjectPhoto.setOnClickListener {
            getPhotoPermission()
        }


        // after post activity then navigate to activity fragment
        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                alertDialog!!.dismiss()
                findNavController().navigate(MainNavigationDirections.navigateToVenueFragment(id))
                viewModel.onLeft()
            }
        })

        return binding.root
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

        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data

                    if (selectedImage != null) {
                        if (selectedImage != null) {
                            val inputStream = context?.contentResolver?.openInputStream(selectedImage)
                            val bitMap = BitmapFactory.decodeStream(inputStream)
                            val fileName = "${randomName(20)}.jpg"

                            // resize image and save into another img
                            bitMap?.let {
                                val resizeImg = getResizedBitmap(bitMap, 1000)
                                val pathSave = Util.saveBitmap(resizeImg!!, fileName)
                                addImageToRecyclerView(resizeImg, pathSave)
                            }
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
        txtDialog.text = "酒名、類別及價格為必填項目"
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
        txtDialog.text = "酒項資訊新增中"

        // set parameter
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}
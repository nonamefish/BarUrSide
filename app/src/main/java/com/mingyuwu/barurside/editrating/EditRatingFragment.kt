package com.mingyuwu.barurside.editrating

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentEditRatingBinding
import com.mingyuwu.barurside.ext.getVmFactory
import kotlin.collections.ArrayList
import android.content.DialogInterface
import android.widget.Button
import com.permissionx.guolindev.PermissionX


private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 101

class EditRatingFragment : Fragment() {

    private var photoPermissionGranted = false
    private lateinit var binding: FragmentEditRatingBinding
    private val viewModel by viewModels<EditRatingViewModel> {
        getVmFactory(
            EditRatingFragmentArgs.fromBundle(requireArguments()).venue
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_rating, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // set adapter
        val adapter = EditRatingAdapter(viewModel)
        binding.venueRtgScoreList.adapter = adapter

        // add drink rating : set button click listener
        binding.btnAddDrinkRtg.setOnClickListener {
            if (viewModel.menu.value != null) {
                addDrinkRating()
            } else {
                Toast.makeText(binding.root.context, "無提供菜單", Toast.LENGTH_SHORT).show()
            }
        }

        // viewModel observer
        viewModel.objectId.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
        )

        // click add photo button
        viewModel.isUploadImgBtn.observe(viewLifecycleOwner, Observer {
            if (photoPermissionGranted) {
                chooseImage(binding.root.context)
            }else{
                getPhotoPermission()
            }
        })

        // get friend list
        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            user.friends?.let {
                viewModel.getFriendList(user)
                Log.d("Ming", "frdList: ${viewModel.frdList.value}")
            }

        })

        // set post rating button click listener
        binding.btnRtgConfirm.setOnClickListener {
            if (viewModel.checkRating()) {
                viewModel.postRating()
            } else {
                showRatingUncompleted()
            }
        }

        // leave rating and to previous view
        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigateUp()
                viewModel.onLeft()
            }
        })


        return binding.root
    }

    // get and check location permission
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
                    photoPermissionGranted=true
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

                            val columnIndex= cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                            cursor.moveToFirst()
                            Log.d("Ming","columnIndex: $columnIndex")
                            val picturePath: String = cursor.getString(columnIndex)
                            Log.d("Ming","picturePath: $picturePath")
                            val img = BitmapFactory.decodeFile(picturePath)
                            addImageToRecyclerView(getResizedBitmap(img, 1000), picturePath)
                            cursor.close()
                        }
                    }
                }
            }
        }
    }

    private fun addImageToRecyclerView(bitmap: Bitmap?, url: String) {
        viewModel.addUploadImg(viewModel.clickPosition.value!!, bitmap, url)
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
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

    private fun addDrinkRating() {
        // set add drink rating adapter
        var menuItem = viewModel.menu.value?.map { it.name }
        val mBuilder = AlertDialog.Builder(binding.root.context)
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_venue_menu, null)
        val spinner = mView.findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            menuItem!!
        )
        spinner.adapter = adapter
        mBuilder.setTitle("選取評價項目")

        // set OK button for alert dialog
        mBuilder.setPositiveButton("OK") { dialog, _ ->
            val selectedPosition = spinner.selectedItemPosition
            viewModel.addNewRating(viewModel.menu.value!![selectedPosition])
            viewModel.removeMenuItem(selectedPosition)

            dialog.dismiss()
        }
        // set Cancel button for alert dialog
        mBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        mBuilder.setView(mView).show()
    }

    private fun showRatingUncompleted() {
        val alertDialog = AlertDialog.Builder(binding.root.context)
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_rating_uncompleted, null)
        val btDialog = mView!!.findViewById<Button>(R.id.button_confirm) //連結關閉視窗的Button

        alertDialog.setView(mView)
        val dialog = alertDialog.create()

        btDialog.setOnClickListener { dialog.dismiss() }
        dialog.show()
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


}


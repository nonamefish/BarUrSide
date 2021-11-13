package com.mingyuwu.barurside.addobject

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.Observer
import android.util.Log
import android.widget.*
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.databinding.FragmentAddObjectBinding
import com.mingyuwu.barurside.discoverdetail.DiscoverDetailFragmentArgs
import com.permissionx.guolindev.PermissionX
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


const val REQUEST_ID_MULTIPLE_PERMISSIONS = 101
private var photoPermissionGranted = false

class AddObjectFragment : Fragment() {

    private lateinit var binding: FragmentAddObjectBinding
    private val viewModel by viewModels<AddObjectViewModel> { getVmFactory(
        AddObjectFragmentArgs.fromBundle(requireArguments()).id
    ) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_object, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        // confirm button
        binding.btnAddActovityConfirm.setOnClickListener {
            if(viewModel.checkValue()){
                viewModel.addDrink()
            }else{
                showAddUncompleted()
            }
        }

        //set spinner type and adapter
        val adapter = ArrayAdapter.createFromResource(
            binding.root.context,
            R.array.drink_type,
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spinnerObjectType.adapter = adapter
        binding.spinnerObjectType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if(position!=0){
                        viewModel.type.value = parent?.getItemAtPosition(position).toString()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        // cancel button
        binding.btnCancel.setOnClickListener {
            findNavController().navigate(MainNavigationDirections.navigateToActivityFragment())
        }

        // add photo
        binding.txtObjectPhoto.setOnClickListener {
            getPhotoPermission()
        }


        // after post activity then navigate to activity fragment
        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            it?.let{
                findNavController().navigate(MainNavigationDirections.navigateToActivityFragment())
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

}
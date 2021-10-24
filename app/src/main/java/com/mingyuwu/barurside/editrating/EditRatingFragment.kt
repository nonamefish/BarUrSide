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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentEditRatingBinding
import com.mingyuwu.barurside.ext.getVmFactory
import kotlin.collections.ArrayList


private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 101

class EditRatingFragment : Fragment() {

    private lateinit var binding: FragmentEditRatingBinding
    private val viewModel by viewModels<EditRatingViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_rating, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // set adapter
        val adapter = EditRatingAdapter(viewModel)
        binding.venueRtgScoreList.adapter = adapter


        // add drink rating : set button click listener
        binding.btnAddDrinkRtg.setOnClickListener {
            viewModel.addNewRating()
        }

        // viewModel observer
        viewModel.rtgList.observe(viewLifecycleOwner, Observer {
            Log.d("Ming","Fragment rtgList.value: $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
            }
        )

        // click add photo button
        viewModel.isUploadImgBtn.observe(viewLifecycleOwner, Observer {
            if (checkAndRequestPermissions(binding.root.context)) {
                chooseImage(binding.root.context)
            }
        })

        // set post rating button click listener
        binding.btnRtgConfirm.setOnClickListener {
            findNavController().navigate(MainNavigationDirections.navigateToActivityFragment())
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> if (
                ContextCompat.checkSelfPermission(
                    binding.root.context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Ming","onRequestPermissionsResult: Here")
            } else {
                chooseImage(binding.root.context)
            }
        }
    }


    private fun checkAndRequestPermissions(context: Context): Boolean {
        val rExtStorePermission: Int = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (rExtStorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(), listPermissionsNeeded
                    .toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
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
                            cursor.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath: String = cursor.getString(columnIndex)
                            val img = BitmapFactory.decodeFile(picturePath)
                            addImageToRecyclerView(img)
                            cursor.close()
                        }
                    }
                }
            }
        }
    }

    private fun addImageToRecyclerView(bitmap: Bitmap?){
        viewModel.addUploadImg(viewModel.clickPosition.value!!, bitmap)
    }
}
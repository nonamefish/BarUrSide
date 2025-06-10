package com.mingyuwu.barurside.editrating

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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentEditRatingBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.ext.isPermissionGranted
import com.mingyuwu.barurside.ext.requestPermission
import com.mingyuwu.barurside.ext.showToast
import com.mingyuwu.barurside.util.AppPermission
import com.mingyuwu.barurside.util.Util
import com.mingyuwu.barurside.util.Util.getResizedBitmap
import com.mingyuwu.barurside.util.Util.randomName
import com.mingyuwu.barurside.util.Util.saveBitmap

class EditRatingFragment : Fragment() {

    private lateinit var binding: FragmentEditRatingBinding
    private val startForGallery = registerStartForGallery()
    private val viewModel by viewModels<EditRatingViewModel> {
        getVmFactory(
            EditRatingFragmentArgs.fromBundle(requireArguments()).venue
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_rating, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // set adapter
        val editRatingAdapter = EditRatingAdapter(viewModel)
        binding.venueRtgScoreList.adapter = editRatingAdapter

        // add drink rating : set button click listener
        binding.btnAddDrinkRtg.setOnClickListener {
            if (viewModel.menu.value != null) {
                addDrinkRating()
            } else {
                showToast("message: ${Util.getString(R.string.no_menu)}")
            }
        }

        // viewModel observer
        viewModel.objectId.observe(viewLifecycleOwner, {
            editRatingAdapter.submitList(it)
            editRatingAdapter.notifyDataSetChanged()
        })

        // click add photo button
        viewModel.isUploadImgBtn.observe(viewLifecycleOwner, {
            if (isPermissionGranted(AppPermission.ReadExternalStorage)) {
                chooseImage(binding.root.context)
            } else {
                requestPermission(AppPermission.ReadExternalStorage)
                chooseImage(binding.root.context)
            }
        })

        // get friend list
        viewModel.user.observe(viewLifecycleOwner, { user ->
            user.friends?.let {
                viewModel.getUsersResultList(user)
            }
        })

        // set post rating button click listener
        var alertDialog: AlertDialog? = null

        binding.btnRtgConfirm.setOnClickListener {
            if (viewModel.checkRating()) {
                viewModel.uploadPhoto()
                alertDialog = postRatingDialog(AlertDialog.Builder(binding.root.context))
            } else {
                showRatingUncompleted()
            }
        }

        // leave rating and to previous view
        viewModel.leave.observe(viewLifecycleOwner, {
            it?.let {
                alertDialog!!.dismiss()
                findNavController().navigateUp()
                viewModel.onLeft()
            }
        })

        return binding.root
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
                startForGallery.launch(pickPhoto)
            } else if (optionsMenu[i] == Util.getString(R.string.exit)) {
                dialogInterface.dismiss()
            }
        }

        builder.show()
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

    private fun addImageToRecyclerView(bitmap: Bitmap?, url: String) {
        viewModel.clickPosition.value?.let {
            viewModel.addUploadImg(it, bitmap, url)
        }
    }

    private fun addDrinkRating() {
        // set dialog view and create dialog
        val mBuilder = AlertDialog.Builder(binding.root.context)
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_venue_menu, null)
        val spinner = mView.findViewById<Spinner>(R.id.spinner)
        mBuilder.setTitle(Util.getString(R.string.add_drink_item))

        // set spinner adapter
        val menuItem = viewModel.menu.value?.map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            menuItem!!
        )
        spinner.adapter = adapter

        // set OK button for alert dialog
        mBuilder.setPositiveButton(Util.getString(R.string.add_drink_confirm)) { dialog, _ ->

            val selectedPosition = spinner.selectedItemPosition

            viewModel.menu.value?.let { it[selectedPosition] }?.let {
                viewModel.addNewRating(
                    it
                )
            }

            viewModel.removeMenuItem(selectedPosition)

            dialog.dismiss()
        }

        // set Cancel button for alert dialog
        mBuilder.setNegativeButton(Util.getString(R.string.add_drink_cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        mBuilder.setView(mView).show()
    }

    private fun showRatingUncompleted() {
        // set dialog view and create dialog
        val alertDialog = AlertDialog.Builder(binding.root.context)
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_rating_uncompleted, null)
        val btDialog = mView!!.findViewById<Button>(R.id.button_confirm)
        alertDialog.setView(mView)
        val dialog = alertDialog.create()

        // dialog button on click listener
        btDialog.setOnClickListener { dialog.dismiss() }
        dialog.show()

        // set windows transparent
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun postRatingDialog(postDialog: AlertDialog.Builder): AlertDialog {
        // set dialog view
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_post_rating, null)
        postDialog.setView(mView)
        val dialog = postDialog.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        // set windows transparent
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 800
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

}

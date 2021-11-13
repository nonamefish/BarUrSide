package com.mingyuwu.barurside.map

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.InfoWindowBinding
import com.mingyuwu.barurside.rating.RatingScoreAdapter
import com.bumptech.glide.request.target.Target
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException

import com.bumptech.glide.request.RequestListener
import android.graphics.Bitmap
import android.text.method.TextKeyListener.clear
import java.lang.Exception
import com.bumptech.glide.request.target.SimpleTarget





class MapInfoWindowAdapter(_context: Context, viewModel: MapViewModel, parent: ViewGroup) :
    GoogleMap.InfoWindowAdapter {
    private val context = _context
    private val viewModel = viewModel
    private val parent = parent
    private lateinit var binding: InfoWindowBinding

    private fun render(marker: Marker, binding: InfoWindowBinding) {
        val info = marker.snippet.toString().split(",")

        // set venue name
        binding.objectName = marker.title

        // score: set adapter
        val scoreAdapter = RatingScoreAdapter(12, 12)
        binding.venueScoreList.adapter = scoreAdapter

        // set average score & rating count
        binding.avgRating = info[1].toDouble()
        binding.shareCount = info[2].toInt()


        binding.imgInfo.setOnClickListener {
            viewModel.navigateToVenue.value = info[0]
        }



        // set venue image
        if (info[3] != null) {
            Glide.with(context)
                .load(info[3])
                .placeholder(R.drawable.image_placeholder)
                .listener(MarkerCallback(marker))
                .into(binding.imgInfo)
        }

        binding.executePendingBindings() // 即時觸發
    }

    override fun getInfoContents(marker: Marker): View {
        // set binding
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = InfoWindowBinding.inflate(layoutInflater, parent, false)
        binding.lifecycleOwner = parent.context as LifecycleOwner

        render(marker, binding)
        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}

class MarkerCallback internal constructor(marker: Marker?) :
    RequestListener<Drawable> {

    var marker: Marker? = null

    private fun onSuccess() {
        if (marker != null && marker!!.isInfoWindowShown) {
            marker!!.hideInfoWindow()
            marker!!.showInfoWindow()
        }
    }

    init {
        this.marker = marker
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        Log.e(javaClass.simpleName, "Error loading thumbnail! -> $e")
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        onSuccess()
        return false
    }
}

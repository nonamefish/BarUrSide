package com.mingyuwu.barurside.map

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.InfoWindowBinding
import com.mingyuwu.barurside.rating.RatingScoreAdapter

class MapInfoWindowAdapter(
    private val context: Context,
    private val viewModel: MapViewModel,
    private val parent: ViewGroup
) : GoogleMap.InfoWindowAdapter {

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
        Glide.with(context)
            .load(info[3])
            .placeholder(R.drawable.image_placeholder)
            .listener(MarkerCallback(marker))
            .into(binding.imgInfo)

        // instant trigger
        binding.executePendingBindings()
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
            marker?.hideInfoWindow()
            marker?.showInfoWindow()
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

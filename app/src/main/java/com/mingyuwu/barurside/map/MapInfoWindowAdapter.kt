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
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.mingyuwu.barurside.GlideApp
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.mockdata.VenueData
import com.mingyuwu.barurside.databinding.FragmentMapBinding
import com.mingyuwu.barurside.databinding.InfoWindowBinding
import com.mingyuwu.barurside.databinding.ItemEditRatingObjectBinding
import com.mingyuwu.barurside.rating.RatingScoreAdapter
import com.mingyuwu.barurside.rating.ScoreStatus
import kotlin.math.roundToInt

class MapInfoWindowAdapter(_context: Context, viewModel: MapViewModel, parent: ViewGroup) :
    GoogleMap.InfoWindowAdapter {

    private val context = _context
    private val viewModel = viewModel
    private val parent = parent
    private lateinit var binding: InfoWindowBinding

    private fun render(marker: Marker, binding: InfoWindowBinding) {
        Log.d("Ming","render")
        Log.d("Ming","marker: ${marker.snippet.toString().split(",")}")
        val info = marker.snippet.toString().split(",")

        // set venue name
        binding.objectName = marker.title

        // score: set adapter
        val scoreAdapter = RatingScoreAdapter(15, 15)
        binding.venueScoreList.adapter = scoreAdapter

        // set average score & rating count
        binding.avgRating = info[1].toDouble()
        binding.shareCount = info[2].toLong()

        // set venue img
        binding.objectImg = info[3]


        binding.imgInfo.setOnClickListener {
            Log.d("Ming", info[1])
            viewModel.navigateToVenue.value = info[0]
        }

    }

    override fun getInfoContents(marker: Marker): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = InfoWindowBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        binding.lifecycleOwner = parent.context as LifecycleOwner

        render(marker, binding)
        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}
package com.mingyuwu.barurside.map

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.databinding.DataBindingUtil
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
import com.mingyuwu.barurside.rating.RatingScoreAdapter
import com.mingyuwu.barurside.rating.ScoreStatus
import kotlin.math.roundToInt

class MapInfoWindowAdapter(_context: Context, viewModel: MapViewModel, container: ViewGroup) :
    GoogleMap.InfoWindowAdapter {

    private val context = _context
    private val viewModel = viewModel
    private val container = container
    private lateinit var binding: InfoWindowBinding

    private fun render(marker: Marker, binding: InfoWindowBinding) {
        Log.d("Ming","render")
        val info = marker.snippet.toString().split(",")

        // set venue name
        binding.infoVenueName.text = marker.title

        // score: set adapter
        val scoreAdapter = RatingScoreAdapter(15, 15)
        binding.venueScoreList.adapter = scoreAdapter

        var starList = listOf<ScoreStatus>()
        for (i in 0 until info[1].toDouble().roundToInt()) {
            starList += ScoreStatus.FULL
        }
        scoreAdapter.submitList(starList)

        // set average score & rating count
        binding.venueAvgScore.text = context.getString(R.string.venue_rating_info_view,info[1].toDouble(),info[2].toLong())

        // set venue img
        info[3]?.let {
            val gsReference = "user/MingYuWu.jpg".let { Firebase.storage.reference.child(it) }
            gsReference.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Ming", "addOnSuccessListener")
                Log.d("Ming", "uri: $uri")

                Glide.with(binding.imgInfo.context)
                    .load(uri)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.imgInfo)
            }
                .addOnFailureListener {
                    Log.d("Ming", it.toString())
                }
        }



        binding.imgInfo.setOnClickListener {
            Log.d("Ming", info[1])
            viewModel.navigateToVenue.value = info[0]
        }

    }

    override fun getInfoContents(marker: Marker): View {
        val binding =
            InfoWindowBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

        render(marker, binding)
        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}
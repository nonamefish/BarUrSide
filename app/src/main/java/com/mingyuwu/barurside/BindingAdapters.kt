package com.mingyuwu.barurside

import android.text.format.DateFormat
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mingyuwu.barurside.rating.RatingScoreAdapter
import com.mingyuwu.barurside.rating.ScoreStatus
import kotlin.math.roundToInt
import com.bumptech.glide.request.RequestOptions
import com.mingyuwu.barurside.rating.ImageAdapter
import java.sql.Timestamp

@BindingAdapter("stars")
fun bindRecyclerViewWithStarts(recyclerView: RecyclerView, stars: Double) {
    stars?.let {
        var starList = listOf<ScoreStatus>()
        for (i in 0 until stars.roundToInt()) {
            starList += ScoreStatus.FULL
        }
        recyclerView.adapter?.apply {
            when (this) {
                is RatingScoreAdapter -> {
                    submitList(starList)
                }
            }
        }

    }
}

@BindingAdapter("imageUrls")
fun bindRecyclerViewWithImageUrls(recyclerView: RecyclerView, imageUrls: List<String>) {
    imageUrls?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ImageAdapter -> {
                    submitList(imageUrls)
                }
            }
        }

    }
}


@BindingAdapter("clickRtgScore")
fun bindClickRtgScore(imageView: ImageView, flgFull: Boolean) {
    flgFull?.let {
        if(flgFull){
            imageView.setBackgroundResource(R.drawable.ic_baseline_star_rate_24)
        }else{
            imageView.setBackgroundResource(R.drawable.ic_baseline_star_border_24)
        }

    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        Log.d("Ming","imgUrl: $imgUrl")
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
            )
            .into(imgView)
    }
}

@BindingAdapter("rtgData")
fun bindRtgData(textView: TextView, timeStamp: Timestamp?) {
    timeStamp?.let {
        textView.text = DateFormat.format("yyyy-MM-dd", timeStamp).toString()
    }
}

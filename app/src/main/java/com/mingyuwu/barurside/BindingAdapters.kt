package com.mingyuwu.barurside

import android.graphics.Bitmap
import android.icu.text.DateFormat.DAY
import android.text.Html
import android.text.format.DateFormat
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.fromHtml
import androidx.core.util.rangeTo
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.Resource
import kotlin.math.roundToInt
import com.bumptech.glide.request.RequestOptions
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.rating.*
import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import kotlin.math.round

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
//                    Log.d("Ming", "RatingScoreAdapter: $starList")
                    submitList(starList)
                }
            }
        }
    }
}

@BindingAdapter("imageUrls")
fun bindRecyclerViewWithImageUrls(recyclerView: RecyclerView, imageUrls: List<String>?) {
    imageUrls?.let {
        if (imageUrls != listOf(null)) {
            recyclerView.adapter?.apply {
                when (this) {
                    is ImageAdapter -> {
                        submitList(imageUrls)
                    }
                    is UserImageAdapter -> {
                        submitList(imageUrls)
                    }
                }
            }
        }
    }
}


@BindingAdapter("clickRtgScore")
fun bindClickRtgScore(imageView: ImageView, flgFull: Boolean?) {
    flgFull?.let {
        if (flgFull) {
            imageView.setBackgroundResource(R.drawable.ic_baseline_star_rate_24)
        } else {
            imageView.setBackgroundResource(R.drawable.ic_baseline_star_border_24)
        }

    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
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

@BindingAdapter("rtgList")
fun bindRtgData(recyclerView: RecyclerView, rtgList: List<Rating>?) {
    rtgList?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is InfoRatingAdapter -> {
                    submitList(rtgList)
                }
            }
        }
    }
}

@BindingAdapter("userImgSize")
fun bindUserImgSize(cardView: CardView, size: Int) {
    size?.let {
        val dpToPx = BarUrSideApplication.appContext!!.resources.displayMetrics.density
        cardView.layoutParams.height = size * dpToPx.toInt()
        cardView.layoutParams.width = size * dpToPx.toInt()
    }
}

@BindingAdapter("imgHeight", "imgWidth")
fun bindImgSize(imageView: ImageView, imgHeight: Int, imgWidth: Int) {
    imgHeight?.let {
        imgWidth?.let {
            val dpToPx = BarUrSideApplication.appContext!!.resources.displayMetrics.density
            imageView.layoutParams.height = imgHeight * dpToPx.toInt()
            imageView.layoutParams.width = imgWidth * dpToPx.toInt()
        }
    }
}

@BindingAdapter("imageBitmap")
fun bindRecyclerViewWithImageBitmap(imageView: ImageView, imageBitmap: Bitmap?) {
    imageBitmap?.let {
        imageView.setImageBitmap(imageBitmap)
    }
}

@BindingAdapter("imageBitmaps")
fun bindRecyclerViewWithImageBitmaps(recyclerView: RecyclerView, imageBitmaps: List<Bitmap>?) {
    imageBitmaps?.let {
        if (imageBitmaps != listOf(null)) {
            recyclerView.adapter?.apply {
                when (this) {
                    is BitmapAdapter -> {
                        submitList(imageBitmaps)
                    }
                }
            }
        }
    }
}

@BindingAdapter("notifyPeriod")
fun bindNotificationPeriod(textView: TextView, date: Timestamp) {
    date?.let {
        val current = Timestamp(System.currentTimeMillis())
        val diff = current.time - date.time
        val diffHour = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS)
        val diffDay = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        when {
            diffHour<0 -> {
                textView.text = "${TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)}分鐘前"
            }
            diffHour<24 -> {
                textView.text = "${diffHour}小時前"
            }
            diffDay<7 -> {
                textView.text = "${diffDay}天前"
            }
            diffDay<30 -> {
                textView.text = "${round(diffDay/7 as Double)+1}週前"
            }
            else -> {
                textView.text = "幾個月前"
            }
        }
    }
}

@BindingAdapter("notifyContent")
fun bindNotificationContent(textView: TextView, content: String) {
    content?.let {
        textView.text = fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

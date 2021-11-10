package com.mingyuwu.barurside

import android.graphics.Bitmap
import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.fromHtml
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlin.math.roundToInt
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.TagFriend
import com.mingyuwu.barurside.rating.*
import com.mingyuwu.barurside.util.CurrentFragmentType
import java.sql.Timestamp
import java.time.LocalTime
import java.util.concurrent.TimeUnit

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

@BindingAdapter("listData")
fun bindRecyclerViewWithListData(recyclerView: RecyclerView, listData: List<Any>?) {
    listData?.let {
        if (listData != listOf(null)) {
            recyclerView.adapter?.apply {
                when (this) {
                    is ImageAdapter -> {
                        submitList((listData as List<String>).toMutableList())
                    }
                    is TagFriendAdapter -> {
                        submitList((listData as List<TagFriend>).toMutableList())
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
    if (!imgUrl.isNullOrEmpty()) {
        Glide.with(imgView.context)
            .load(imgUrl)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(imgView)
    } else {
        imgView.setBackgroundResource(R.drawable.image_placeholder)
    }
}

@BindingAdapter("rtgDate")
fun bindRtgDate(textView: TextView, timeStamp: Timestamp?) {
    timeStamp?.let {
        textView.text = DateFormat.format("yyyy-MM-dd", timeStamp).toString()
    }
}

@BindingAdapter("rtgList")
fun bindRtgList(recyclerView: RecyclerView, rtgList: List<RatingInfo>?) {
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
                        Log.d("Ming", "imageBitmaps: $imageBitmaps")
                        submitList(imageBitmaps)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }
}

@BindingAdapter("notifyPeriod")
fun bindNotificationPeriod(textView: TextView, date: Timestamp?) {
    date?.let {
        val current = Timestamp(System.currentTimeMillis())
        val diff = current.time - date.time
        val diffHour = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS)
        val diffDay = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        when {
            diffHour < 0 -> {
                textView.text = "${TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)}分鐘前"
            }
            diffHour < 24 -> {
                textView.text = "${diffHour}小時前"
            }
            diffDay < 7 -> {
                textView.text = "${diffDay}天前"
            }
            diffDay < 30 -> {
                textView.text = "${(diffDay.toDouble() / 7).toString().substringBefore(".")}週前"
            }
            else -> {
                textView.text = "幾個月前"
            }
        }
    }
}

@BindingAdapter("notifyContent")
fun bindNotificationContent(textView: TextView, content: String?) {
    content?.let {
        textView.text = fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("isOpen")
fun bindIsOpen(textView: TextView, serviceTime: String?) {
    serviceTime?.let {
        val open = serviceTime.split("-")[0]
        val close = serviceTime.split("-")[1]
        when (checkTime(open, close)) {
            true -> {
                if (open.split(":")[0].toInt() < close.split(":")[0].toInt()) {
                    textView.text = "營業中 直至${close}"
                } else {
                    textView.text = "營業中 直至明日${close}"
                }
            }
            false -> {
                if (open.split(":")[0].toInt() < close.split(":")[0].toInt()) {
                    textView.text = "休息中 開始營業時間：${open}"
                } else {
                    textView.text = "休息中 開始營業時間：明日${open}"
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun checkTime(open: String, close: String): Boolean {

    val open = LocalTime.parse(open)
    val close = LocalTime.parse(close)
    val current = LocalTime.now()

    return current.isAfter(open) && current.isBefore(close)
}

@BindingAdapter("activityTime")
fun bindTimeActivityTime(textView: TextView, activityTime: Timestamp?) {
    activityTime?.let {
        textView.text = DateFormat.format("yyyy/MM/dd a hh:mm", activityTime).toString()
    }
}

@BindingAdapter("iconNotify")
fun bindIconNotify(imageView: ImageView, currentFragmentType: CurrentFragmentType?) {
    currentFragmentType?.let {
        if (currentFragmentType in listOf(
                CurrentFragmentType.DISCOVER_DETAIL,
                CurrentFragmentType.EDIT_RATING,
                CurrentFragmentType.ADD_ACTIVITY,
                CurrentFragmentType.DRINK,
                CurrentFragmentType.VENUE
            )
        ){
            imageView.visibility = View.GONE
        }else{
            imageView.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("iconBack")
fun bindIconBack(imageView: ImageView, currentFragmentType: CurrentFragmentType?) {
    currentFragmentType?.let {
        if (currentFragmentType !in listOf(
                CurrentFragmentType.DISCOVER_DETAIL,
                CurrentFragmentType.EDIT_RATING,
                CurrentFragmentType.DRINK,
                CurrentFragmentType.VENUE
            )
        ){
            imageView.visibility = View.GONE
        }else{
            imageView.visibility = View.VISIBLE
        }
    }
}

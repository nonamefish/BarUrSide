package com.mingyuwu.barurside

import android.graphics.Bitmap
import android.os.Build
import android.text.format.DateFormat
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.fromHtml
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.TagFriend
import com.mingyuwu.barurside.rating.*
import com.mingyuwu.barurside.util.CurrentFragmentType
import com.mingyuwu.barurside.util.Style
import com.mingyuwu.barurside.util.Util
import com.mingyuwu.barurside.util.Util.getDiffDay
import com.mingyuwu.barurside.util.Util.getDiffHour
import com.mingyuwu.barurside.util.Util.getDiffMinute
import com.mingyuwu.barurside.venue.MenuAdapter
import java.sql.Timestamp
import java.time.LocalTime
import java.util.*
import kotlin.math.roundToInt

@BindingAdapter("stars")
fun bindRecyclerViewWithStarts(recyclerView: RecyclerView, stars: Double) {
    val starList = mutableListOf<ScoreStatus>()
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

@BindingAdapter("listData")
fun bindRecyclerViewWithListData(recyclerView: RecyclerView, listData: List<Any>?) {
    listData?.let {
        if (listData != listOf(null)) {
            recyclerView.adapter?.apply {
                when (this) {
                    is ImageAdapter -> {
                        submitList((listData).toMutableList())
                    }
                    is TagFriendAdapter -> {
                        val list = listData.filterIsInstance<TagFriend>()
                        submitList(list.toMutableList())
                    }
                    is MenuAdapter -> {
                        val list = listData.filterIsInstance<Drink>()
                        submitList(
                            list.toMutableList().sortedByDescending { it.avgRating }.take(10)
                        )
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
    size.let {
        val dpToPx = BarUrSideApplication.appContext!!.resources.displayMetrics.density
        cardView.layoutParams.height = size * dpToPx.toInt()
        cardView.layoutParams.width = size * dpToPx.toInt()
    }
}

@BindingAdapter("imgHeight", "imgWidth")
fun bindImgSize(imageView: ImageView, imgHeight: Int, imgWidth: Int) {
    val dpToPx = BarUrSideApplication.appContext!!.resources.displayMetrics.density
    imageView.layoutParams.height = imgHeight * dpToPx.toInt()
    imageView.layoutParams.width = imgWidth * dpToPx.toInt()
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
        val diffHour = getDiffHour(date)
        val diffDay = getDiffDay(date)
        val diffWeek = (diffDay.toDouble() / 7).toString().substringBefore(".").toInt()
        when {
            diffHour < 0 -> {
                textView.text = Util.getString(R.string.minute_ago, getDiffMinute(date))
            }
            diffHour < 24 -> {
                textView.text =
                    Util.getString(R.string.hour_ago, diffHour)
            }
            diffDay < 7 -> {
                textView.text = Util.getString(R.string.day_ago, diffDay)
            }
            diffDay < 30 -> {
                textView.text = Util.getString(
                    R.string.week_ago,
                    diffWeek
                )
            }
            else -> {
                textView.text = Util.getString(R.string.month_ago)
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

@BindingAdapter("isOpen")
fun bindIsOpen(textView: TextView, serviceTime: String?) {
    serviceTime?.let {
        val open = serviceTime.split("-")[0]
        val close = serviceTime.split("-")[1]
        val current = LocalTime.now()
        when (checkTime(open, close)) {
            true -> {
                if (open.split(":")[0].toInt() < close.split(":")[0].toInt()) {
                    textView.text = Util.getString(R.string.service_until_today, close)
                } else {
                    textView.text = Util.getString(R.string.service_until_tomorrow, close)
                }
            }
            false -> {
                if (current.isBefore(LocalTime.parse(open))) {
                    textView.text = Util.getString(R.string.rest_until_today, open)
                } else {
                    textView.text = Util.getString(R.string.rest_until_tomorrow, open)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun checkTime(open: String, close: String): Boolean {
    val openTime = LocalTime.parse(open)
    val closeTime = LocalTime.parse(close)
    val current = LocalTime.now()

    return current.isAfter(openTime) && current.isBefore(closeTime)
}

@BindingAdapter("activityTime")
fun bindTimeActivityTime(textView: TextView, activityTime: Date?) {
    activityTime?.let {
        textView.text = DateFormat.format(
            Util.getString(R.string.datetime_format), activityTime
        ).toString()
    }
}

@BindingAdapter("iconNotify")
fun bindIconNotify(constraintLayout: ConstraintLayout, currentFragmentType: CurrentFragmentType?) {
    currentFragmentType?.let {
        if (currentFragmentType in listOf(
                CurrentFragmentType.ACTIVITY,
                CurrentFragmentType.DISCOVER,
                CurrentFragmentType.COLLECT
            )
        ) {
            constraintLayout.visibility = View.VISIBLE
        } else {
            constraintLayout.visibility = View.GONE
        }
    }
}

@BindingAdapter("iconReport")
fun bindIconReport(imageView: ImageView, currentFragmentType: CurrentFragmentType?) {
    currentFragmentType?.let {
        if (currentFragmentType in listOf(
                CurrentFragmentType.OTHER_PROFILE,
                CurrentFragmentType.USER_PROFILE
            )
        ) {
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("iconBack")
fun bindIconBack(imageView: ImageView, currentFragmentType: CurrentFragmentType?) {
    currentFragmentType?.let {
        if (currentFragmentType !in listOf(
                CurrentFragmentType.DISCOVER_DETAIL,
                CurrentFragmentType.OTHER_PROFILE,
                CurrentFragmentType.EDIT_RATING,
                CurrentFragmentType.ALL_RATING,
                CurrentFragmentType.DRINK,
                CurrentFragmentType.VENUE
            )
        ) {
            imageView.visibility = View.GONE
        } else {
            imageView.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("venueStyle")
fun bindVenueStyle(textView: TextView, style: String?) {
    style?.let {
        textView.text = Style.valueOf(it.uppercase()).chinese
    }
}

@BindingAdapter("isFull", "hasBook")
fun bindActivityDetailBtn(button: Button, isFull: Boolean, hasBook: Boolean) {
    when {
        isFull -> {
            button.text = Util.getString(R.string.activity_full)
            button.isEnabled = false
        }
        hasBook -> {
            button.text = Util.getString(R.string.activity_quit)
            button.isEnabled = true
        }
        else -> {
            button.text = Util.getString(R.string.activity_join)
            button.isEnabled = true
        }
    }
}

@BindingAdapter("distance")
fun bindDistance(textView: TextView, distance: Int?) {
    distance?.let {
        if (it < 1000) {
            textView.text = Util.getString(R.string.distance_meter, distance)
        } else {
            textView.text = Util.getString(
                R.string.distance_kilometer,
                String.format("%.1f", (it.toDouble() / 1000))
            )
        }
    }
}

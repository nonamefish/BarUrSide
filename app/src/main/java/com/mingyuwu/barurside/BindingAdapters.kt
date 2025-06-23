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

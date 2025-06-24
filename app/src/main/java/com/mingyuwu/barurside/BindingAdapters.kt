package com.mingyuwu.barurside

import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.mingyuwu.barurside.util.CurrentFragmentType


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

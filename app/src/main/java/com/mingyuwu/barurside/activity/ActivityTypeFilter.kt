package com.mingyuwu.barurside.activity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ActivityTypeFilter(val type: String) : Parcelable {
    RECOMMEND("recommend"),
    ACTIVITY("activity"),
    FOLLOW("follow")
}

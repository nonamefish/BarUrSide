package com.mingyuwu.barurside.filter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterParameter(
    val level: Number?,
    val category: List<String>?,
    val style: List<String>?,
    val distance: Double?
) : Parcelable

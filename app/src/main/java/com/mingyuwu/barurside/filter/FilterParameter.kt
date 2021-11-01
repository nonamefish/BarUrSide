package com.mingyuwu.barurside.filter

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.data.Relationship
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class FilterParameter(
    val level: Number?,
    val category: List<String>?,
    val style: List<String>?,
    val distance: Double?
): Parcelable
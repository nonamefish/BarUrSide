package com.mingyuwu.barurside.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class Venue(
    val id: String,
    val menuId: String,
    val name: String,
    val style: String,
    val level: Number,
    val serviceTime: String,
    val web: String,
    val phone: String,
    val address: String,
    val location: LatLng,
    val images: List<String>,
    val avgRating: Double,
    val rtgCount: Number
) : Parcelable
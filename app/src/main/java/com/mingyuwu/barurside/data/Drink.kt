package com.mingyuwu.barurside.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class Drink(
    val id: String,
    val menuId: String,
    val venueId: String,
    val name: String,
    val category: String,
    val price: Number,
    val images: List<String>,
    val avgRating: Double,
    val rtgCount: Number
) : Parcelable
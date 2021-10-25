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
) : Parcelable{
    companion object{
        fun toHashMap(dt : Drink) = hashMapOf(
            "id" to dt.id,
            "menuId" to dt.menuId,
            "venueId" to dt.venueId,
            "name" to dt.name,
            "category" to dt.category,
            "price" to dt.price,
            "images" to dt.images,
            "avgRating" to dt.avgRating,
            "rtgCount" to dt.rtgCount
        )
    }
}
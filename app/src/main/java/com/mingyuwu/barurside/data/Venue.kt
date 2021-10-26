package com.mingyuwu.barurside.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class Venue(
    val id: String = "",
    val menuId: String = "",
    val name: String = "",
    val style: String = "",
    val level: Long? = -1,
    val serviceTime: String = "",
    val web: String = "",
    val phone: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val images: List<String>? = null,
    val avgRating: Double = -1.0,
    val rtgCount: Long? = -1
) : Parcelable {

    companion object {
        fun getLatLng(dt: Venue) = LatLng(dt.latitude,dt.longitude)

        fun toHashMap(dt: Venue) = hashMapOf(
            "id" to dt.id,
            "menuId" to dt.menuId,
            "name" to dt.name,
            "style" to dt.style,
            "level" to dt.level,
            "serviceTime" to dt.serviceTime,
            "web" to dt.web,
            "phone" to dt.phone,
            "address" to dt.address,
            "latitude" to dt.latitude,
            "longitude" to dt.longitude,
            "images" to dt.images,
            "avgRating" to dt.avgRating,
            "rtgCount" to dt.rtgCount
        )
    }
}
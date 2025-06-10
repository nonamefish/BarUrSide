package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Drink(
    var id: String = "",
    val menuId: String = "",
    val venueId: String = "",
    val name: String = "",
    val category: String = "",
    val price: Long? = -1,
    val description: String = "",
    val images: List<String>? = null,
    val avgRating: Double = -1.0,
    val rtgCount: Long? = -1
) : Parcelable {

    companion object {
        fun toHashMap(dt: Drink) = hashMapOf(
            "id" to dt.id,
            "menuId" to dt.menuId,
            "venueId" to dt.venueId,
            "name" to dt.name,
            "category" to dt.category,
            "price" to dt.price,
            "description" to dt.description,
            "images" to dt.images,
            "avgRating" to dt.avgRating,
            "rtgCount" to dt.rtgCount
        )
    }
}

package com.mingyuwu.barurside.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp
import java.util.*

@Parcelize
data class Rating(
    val id: String = "",
    val objectId: String = "",
    val isVenue: Boolean? = null,
    val userId: String = "",
    val rating: Long? = -1,
    val comment: String = "",
    val images: List<String>? = null,
    val postDate: Date? = null,
    val tagFriends: List<String>? = null
) : Parcelable {

    val postTimestamp = postDate?.let { Timestamp(it.time) }
    companion object {

        fun toHashMap(dt: Rating) = hashMapOf(
            "id" to dt.id,
            "objectId" to dt.objectId,
            "isVenue" to dt.isVenue,
            "userId" to dt.userId,
            "rating" to dt.rating,
            "comment" to dt.comment,
            "images" to dt.images,
            "postDate" to dt.postDate,
            "tagFriends" to dt.tagFriends
        )
    }
}
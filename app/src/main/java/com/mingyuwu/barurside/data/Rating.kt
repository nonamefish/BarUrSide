package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp
import java.util.*

@Parcelize
data class Rating(
    var id: String = "",
    val objectId: String = "",
    @JvmField val isVenue: Boolean? = null,
    val userId: String = "",
    val rating: Long? = -1,
    val comment: String = "",
    val images: List<String>? = null,
    var postDate: Date? = null,
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

@Parcelize
data class RatingInfo(
    var id: String = "",
    val objectId: String = "",
    @JvmField val isVenue: Boolean? = null,
    val userId: String = "",
    val rating: Long? = -1,
    val comment: String = "",
    val images: List<String>? = null,
    var postDate: Date? = null,
    val tagFriends: List<String>? = null,
    // add column
    var objectName: String? = null,
    var objectImg: String? = null,
    var userInfo: User? = null,
    val tagFriendsImg: List<String>? = null
) : Parcelable {
    var postTimestamp = postDate?.let { Timestamp(it.time) }
}
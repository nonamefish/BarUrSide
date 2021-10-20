package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class Rating(
    val id: String,
    val objectId: String,
    val isVenue: Boolean,
    val userId: String,
    val rating: Number,
    val comment: String,
    val images: List<String>,
    val postDate: Timestamp,
    val tagFriends: List<String>
) : Parcelable
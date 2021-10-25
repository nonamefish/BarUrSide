package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class User(
    val id: String,
    val name: String,
    val image: String,
    val friends: List<Relationship>,
    val shareCount: Number,
    val shareImageCount: Number
) : Parcelable{
    companion object{
        fun toHashMap(dt : User) = hashMapOf(
            "id" to dt.id,
            "name" to dt.name,
            "image" to dt.image,
            "friends" to dt.friends,
            "shareCount" to dt.shareCount,
            "shareImageCount" to dt.shareImageCount
        )
    }
}
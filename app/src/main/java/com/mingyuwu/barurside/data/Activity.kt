package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class Activity(
    val id: String,
    val name: String,
    val startTime: Timestamp,
    val endTime: Timestamp,
    val address: String,
    val peopleLimit: Number,
    val mainDrinking: String,
    val sponsor: String,
    val bookers: List<Relationship>
): Parcelable {
    companion object{
        fun toHashMap(dt : Activity) = hashMapOf(
            "id" to dt.id,
            "name" to dt.name,
            "startTime" to dt.startTime,
            "endTime" to dt.endTime,
            "address" to dt.address,
            "peopleLimit" to dt.peopleLimit,
            "mainDrinking" to dt.mainDrinking,
            "sponsor" to dt.sponsor,
            "bookers" to dt.bookers
        )
    }
}
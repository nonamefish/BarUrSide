package com.mingyuwu.barurside.data

import android.os.Parcelable
import java.sql.Timestamp
import java.util.Date
import kotlinx.parcelize.Parcelize

@Parcelize
data class Activity(
    var id: String = "",
    val name: String = "",
    val startTime: Date? = null,
    val endTime: Date? = null,
    val address: String = "",
    val peopleLimit: Long? = -1,
    val mainDrinking: String = "",
    val sponsor: String = "",
    val bookers: List<Relationship>? = null,
) : Parcelable {

    var startTimestamp = startTime?.let { Timestamp(it.time) }
    var endTimestamp = endTime?.let { Timestamp(it.time) }

    companion object {
        fun toHashMap(dt: Activity) = hashMapOf(
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

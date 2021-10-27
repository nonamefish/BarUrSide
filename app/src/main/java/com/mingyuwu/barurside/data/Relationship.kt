package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp
import java.util.*

@Parcelize
class Relationship(
    val id: String = "",
    val date: Date? = null
) : Parcelable{
    val dateTimestamp = date?.let { Timestamp(it.time) }
    companion object{
        fun toHashMap(dt : Relationship) = hashMapOf(
            "id" to dt.id,
            "date" to dt.date
        )
    }
}
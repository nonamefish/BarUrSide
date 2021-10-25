package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
class Relationship(
    val id: String,
    val date: Timestamp
) : Parcelable{
    companion object{
        fun toHashMap(dt : Relationship) = hashMapOf(
            "id" to dt.id,
            "date" to dt.date
        )
    }
}
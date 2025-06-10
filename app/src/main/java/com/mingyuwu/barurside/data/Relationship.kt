package com.mingyuwu.barurside.data

import android.os.Parcelable
import java.util.Date
import kotlinx.parcelize.Parcelize

@Parcelize
class Relationship(
    val id: String = "",
    val date: Date? = null
) : Parcelable {
    companion object {
        fun toHashMap(dt: Relationship) = hashMapOf(
            "id" to dt.id,
            "date" to dt.date
        )
    }
}

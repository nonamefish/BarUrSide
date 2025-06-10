package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Report(
    val id: String = "",
    val type: String = ""
) : Parcelable {
    companion object {
        fun toHashMap(dt: Report) = hashMapOf(
            "id" to dt.id,
            "type" to dt.type
        )
    }
}
package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Collect (
    val id: String,
    val useId: String,
    val objectId: String,
    val isVenue: Boolean,
): Parcelable{
    companion object{
        fun toHashMap(dt : Collect) = hashMapOf(
            "id" to dt.id,
            "useId" to dt.useId,
            "objectId" to dt.objectId,
            "isVenue" to dt.isVenue
        )
    }
}
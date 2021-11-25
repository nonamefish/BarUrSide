package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Collect(
    var id: String = "",
    @JvmField val isVenue: Boolean? = null,
    val userId: String = "",
    val objectId: String = ""
) : Parcelable {

    companion object {
        fun toHashMap(dt: Collect) = hashMapOf(
            "id" to dt.id,
            "userId" to dt.userId,
            "objectId" to dt.objectId,
            "isVenue" to dt.isVenue
        )
    }
}

package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TagFriend(
    val id: String? = "",
    val name: String? = ""
) : Parcelable {
    companion object {
        fun toHashMap(dt: TagFriend) = hashMapOf(
            "id" to dt.id,
            "name" to dt.name
        )
    }
}

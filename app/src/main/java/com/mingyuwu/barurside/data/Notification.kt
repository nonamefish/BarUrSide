package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp
import java.util.*

@Parcelize
data class Notification(
    var id: String= "",
    val objectId: String= "",
    val image: String= "",
    val type: String= "",
    val date: Date? = null,
    val fromId: String= "",
    val toId: String= "",
    val content: String= "",
    val isReply: Boolean? = null,
    val isCheck: Boolean? = null
) : Parcelable{

    var timestamp = date?.let { Timestamp(it.time) }

    companion object{
        fun toHashMap(dt : Notification) = hashMapOf(
            "id" to dt.id,
            "objectId" to dt.objectId,
            "image" to dt.image,
            "type" to dt.type,
            "date" to dt.date,
            "fromId" to dt.fromId,
            "toId" to dt.toId,
            "content" to dt.content,
            "isReply" to dt.isReply
        )
    }
}
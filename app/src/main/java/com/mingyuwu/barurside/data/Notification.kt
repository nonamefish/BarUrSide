package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class Notification(
    val id: String,
    val objectId: String,
    val type: String,
    val date: Timestamp,
    val fromId: String,
    val toId: String,
    val content: String,
    val isReply: Boolean
) : Parcelable
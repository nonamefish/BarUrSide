package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class Activity(
    val id: String,
    val name: String,
    val startTime: Timestamp,
    val endTime: Timestamp,
    val address: String,
    val peopleLimit: Number,
    val mainDrinking: String,
    val sponsor: String,
    val bookers: List<Relationship>
): Parcelable
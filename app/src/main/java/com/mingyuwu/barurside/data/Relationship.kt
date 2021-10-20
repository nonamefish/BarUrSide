package com.mingyuwu.barurside.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
class Relationship(
    val id: String,
    val date: Timestamp
) : Parcelable
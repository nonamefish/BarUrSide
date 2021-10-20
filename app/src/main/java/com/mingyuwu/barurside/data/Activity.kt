package com.mingyuwu.barurside.data

import java.sql.Timestamp

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
)
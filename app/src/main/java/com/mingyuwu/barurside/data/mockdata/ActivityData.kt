package com.mingyuwu.barurside.data.mockdata

import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Collect
import com.mingyuwu.barurside.data.Relationship
import java.sql.Timestamp

class ActivityData {
    object activity {
        val activity1 = listOf(
            Activity(
                "1",
                "酒中豪傑闖天下",
                Timestamp(System.currentTimeMillis()),
                Timestamp(System.currentTimeMillis()),
                "新北市新莊區中正路1號",
                30,
                "高粱酒",
                "1",
                listOf(
                    Relationship("2", Timestamp(System.currentTimeMillis())),
                    Relationship("3", Timestamp(System.currentTimeMillis()))
                )
            )
        )
    }
}
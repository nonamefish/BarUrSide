package com.mingyuwu.barurside.data.mockdata

import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Relationship
import java.sql.Timestamp
import java.util.*

class ActivityData {
    companion object {
        fun getAfterDate(): Timestamp {
            val ts = Timestamp(System.currentTimeMillis())
            val cal = Calendar.getInstance();
            cal.time = ts;
            cal.add(Calendar.DAY_OF_WEEK, 14);

            return Timestamp(cal.time.time);
        }
    }

    object activity {
        val activity = listOf(
            Activity(
                "1",
                "酒中豪傑闖天下",
                Timestamp(System.currentTimeMillis()),
                getAfterDate(),
                "新北市新莊區中正路1號",
                30,
                "高粱酒",
                "1",
                listOf(
                    Relationship("2", Timestamp(System.currentTimeMillis())),
                    Relationship("3", Timestamp(System.currentTimeMillis()))
                )
            ),
            Activity(
                "2",
                "酒中豪女闖天下",
                Timestamp(System.currentTimeMillis()),
                getAfterDate(),
                "新北市新莊區中正路2號",
                30,
                "白蘭地",
                "2",
                listOf(
                    Relationship("2", Timestamp(System.currentTimeMillis())),
                    Relationship("3", Timestamp(System.currentTimeMillis()))
                )
            )
        )
    }
}
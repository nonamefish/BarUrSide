package com.mingyuwu.barurside.data.mockdata

import com.mingyuwu.barurside.data.Relationship
import com.mingyuwu.barurside.data.User
import java.sql.Timestamp

class UserData {
    object user {
        val user = listOf(
            User(
                "1", "John", "",
                listOf(
                    Relationship("2", Timestamp(System.currentTimeMillis())),
                    Relationship("3", Timestamp(System.currentTimeMillis()))
                ),
                listOf(
                    Relationship("4", Timestamp(System.currentTimeMillis())),
                    Relationship("5", Timestamp(System.currentTimeMillis()))
                ),
                6, 7
            ),
            User(
                "11", "Sharon", "",
                listOf(
                    Relationship("21", Timestamp(System.currentTimeMillis())),
                    Relationship("31", Timestamp(System.currentTimeMillis()))
                ),
                listOf(
                    Relationship("41", Timestamp(System.currentTimeMillis())),
                    Relationship("51", Timestamp(System.currentTimeMillis()))
                ),
                6, 7
            )
        )
    }

}
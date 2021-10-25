package com.mingyuwu.barurside.data.mockdata

import com.mingyuwu.barurside.data.Notification
import java.sql.Timestamp

class NotificationData {
    object notification {
        val notification = listOf(
            Notification(
                "1",
                "11",
                "",
                "Friend Request",
                Timestamp(1635004800274),
                "1",
                "1",
                "<b>無尾熊</b>想要加你好友",
                false
            ),
            Notification(
                "2",
                "11",
                "",
                "Friend Request",
                Timestamp(1635005300274),
                "1",
                "1",
                "<b>Jason Mraz</b>想要加你好友",
                false
            )
        )
    }
}
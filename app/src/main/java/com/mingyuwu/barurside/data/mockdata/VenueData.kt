package com.mingyuwu.barurside.data.mockdata

import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Relationship
import com.mingyuwu.barurside.data.Venue
import java.sql.Timestamp

class VenueData {
    object venue {
        val venue = listOf(
            Venue(
                "11111111",
                "1",
                "楊泉酒家",
                "酒館",
                3,
                "10:00-22:00",
                "https://www.google.com.tw/",
                "02-12345678",
                "新北市新莊區中正路1號",
                23.2,
                111.1,
                listOf("", ""),
                3.5,
                123
            ),
            Venue(
                "2",
                "2",
                "楊泉扮家家酒家",
                "酒館",
                4,
                "10:00-22:00",
                "https://www.google.com.tw/",
                "02-12345678",
                "新北市新莊區中正路2號",
                23.2,
                111.1,
                listOf("", ""),
                3.5,
                123
            )
        )
    }
}
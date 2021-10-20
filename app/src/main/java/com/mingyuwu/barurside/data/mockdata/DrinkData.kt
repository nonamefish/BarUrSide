package com.mingyuwu.barurside.data.mockdata

import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Venue
import kotlinx.parcelize.Parcelize

class DrinkData {
    object drink {
        val drink = listOf(
            Drink(
                "1",
                "1",
                "楊泉酒家",
                "Old Fashion",
                "雞尾酒",
                123,
                listOf(""),
                3.9,
                123
            ),
            Drink(
                "2",
                "1",
                "楊泉酒家",
                "Gin tonic",
                "雞尾酒",
                123,
                listOf(""),
                3.9,
                666
            )
        )
    }
}
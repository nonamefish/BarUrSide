package com.mingyuwu.barurside.data.mockdata

import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.RatingInfo
import java.sql.Timestamp

class RatingData {
    object rating {
        val rating = listOf(
            Rating(
                "1",
                "1",
                true,
                "1",
                3,
                "台股今 (19) 日受惠電子股人氣回籠、航運股走勢趨穩， 由 CCL、ABF 族群領軍上攻，加權股價指數早盤一度大漲超過 200 點，市場專家認為，以技術指標來看，台股反彈尚未結束，可望進一步挑戰季線及半年線。",
                listOf(
                    "https://upload.wikimedia.org/wikipedia/commons/4/43/Bar-P1030319.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/4/43/Bar-P1030319.jpg"
                ),
                Timestamp(System.currentTimeMillis()),
                listOf("2", "3")
            ),
            Rating(
                "2",
                "1",
                false,
                "1",
                3,
                "台股今天開高走高，早盤以 16791.12 點開出，最高攻抵 16912.24 點，最低 16772.15 點，預估市場全場成交值為 2677 億元。",
                listOf(
                    "https://upload.wikimedia.org/wikipedia/commons/3/30/Swallow_flying_drinking_2.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/3/30/Swallow_flying_drinking_2.jpg"
                ),
                Timestamp(System.currentTimeMillis()),
                listOf("2", "3")
            ),
        )
    }
}

class RatingInfoData {
    object rating {
        val rating = listOf(
            RatingInfo(
                "1",
                "1",
                true,
                "1",
                3,
                "台股今 (19) 日受惠電子股人氣回籠、航運股走勢趨穩， 由 CCL、ABF 族群領軍上攻，加權股價指數早盤一度大漲超過 200 點，市場專家認為，以技術指標來看，台股反彈尚未結束，可望進一步挑戰季線及半年線。",
                listOf(
                    "https://upload.wikimedia.org/wikipedia/commons/4/43/Bar-P1030319.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/4/43/Bar-P1030319.jpg"
                ),
                Timestamp(System.currentTimeMillis()),
                listOf("2", "3")
            ),
            RatingInfo(
                "2",
                "1",
                false,
                "1",
                3,
                "台股今天開高走高，早盤以 16791.12 點開出，最高攻抵 16912.24 點，最低 16772.15 點，預估市場全場成交值為 2677 億元。",
                listOf(
                    "https://upload.wikimedia.org/wikipedia/commons/3/30/Swallow_flying_drinking_2.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/3/30/Swallow_flying_drinking_2.jpg"
                ),
                Timestamp(System.currentTimeMillis()),
                listOf("2", "3")
            ),
        )
    }
}
package com.mingyuwu.barurside.util

import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.util.Util.getString


enum class CurrentFragmentType(val value: String) {
    ACTIVITY(getString(R.string.activity_title)),
    ADD_ACTIVITY("新增活動"),
    COLLECT(getString(R.string.collect_title)),
    DISCOVER(getString(R.string.discover_title)),
    DISCOVER_DETAIL("搜尋結果"),
    DRINK(getString(R.string.drink_title)),
    VENUE(getString(R.string.venue_title)),
    EDIT_RATING("編輯評論"),
    MAP(""),
    PROFILE(getString(R.string.profile_title)),
    ALL_RATING(getString(R.string.all_rating_title)),
    LOGIN("")
}
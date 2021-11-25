package com.mingyuwu.barurside.util

import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.util.Util.getString


enum class CurrentFragmentType(val value: String?) {
    ACTIVITY(getString(R.string.activity_title)),
    ADD_ACTIVITY(getString(R.string.add_activity_tile)),
    ADD_OBJECT(getString(R.string.add_object_tile)),
    COLLECT(getString(R.string.collect_title)),
    DISCOVER(getString(R.string.discover_title)),
    DISCOVER_DETAIL(null),
    DRINK(getString(R.string.drink_title)),
    VENUE(getString(R.string.venue_title)),
    EDIT_RATING(getString(R.string.edit_rating_title)),
    MAP(null),
    PROFILE(getString(R.string.profile_title)),
    ALL_RATING(getString(R.string.all_rating_title)),
    LOGIN(null)
}
package com.mingyuwu.barurside.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.mockdata.ActivityData
import com.mingyuwu.barurside.data.mockdata.DrinkData
import com.mingyuwu.barurside.data.mockdata.RatingData
import com.mingyuwu.barurside.data.mockdata.VenueData
import com.mingyuwu.barurside.rating.InfoRatingAdapter

class ActivityPageViewModel(val type: ActivityTypeFilter): ViewModel() {

    private val _activityData = MutableLiveData<List<Any>>()
    val activityData: MutableLiveData<List<Any>> // why cannot use LiveData<List<Any>>
        get() = _activityData

    val navigateToDetail = MutableLiveData<Any?>()


    init{
        when (type) {
            ActivityTypeFilter.RECOMMEND -> {
                _activityData.value= RatingData.rating.rating
            }
            ActivityTypeFilter.ACTIVITY -> {
                _activityData.value= ActivityData.activity.activity
            }
            ActivityTypeFilter.FOLLOW -> {
                _activityData.value= RatingData.rating.rating
            }
        }
    }
}
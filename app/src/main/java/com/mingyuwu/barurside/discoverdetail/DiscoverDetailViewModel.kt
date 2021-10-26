package com.mingyuwu.barurside.discoverdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.data.mockdata.*
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.filter.FilterParameter

class DiscoverDetailViewModel(
    val repository: BarUrSideRepository,
    val theme: Theme,
    val filterParameter: FilterParameter?
) :
    ViewModel() {

    private val _detailData = MutableLiveData<List<Any>>()
    val detailData: MutableLiveData<List<Any>> // why cannot use LiveData<List<Any>>
        get() = _detailData

    val navigateToInfo = MutableLiveData<Any>()

    init {

        when (theme.order) {
            1 -> {
                _detailData.value = ActivityData.activity.activity
            }
            8 -> {
                _detailData.value = ActivityData.activity.activity
            }
            6 -> {
                _detailData.value = VenueData.venue.venue
            }
            7 -> {
                _detailData.value = UserData.user.user
            }
            9 -> {
                _detailData.value = NotificationData.notification.notification
            }
            10 -> {
                _detailData.value = DrinkData.drink.drink
            }
            in arrayOf(0, 3, 5) -> {
                _detailData.value = VenueData.venue.venue
            }
            in arrayOf(2, 4) -> {
                _detailData.value = DrinkData.drink.drink
            }
        }
    }
}
package com.mingyuwu.barurside.discoverdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.mockdata.ActivityData
import com.mingyuwu.barurside.data.mockdata.DrinkData
import com.mingyuwu.barurside.data.mockdata.VenueData
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.filter.FilterParameter

class DiscoverDetailViewModel(val theme: Theme, val filterParameter: FilterParameter?) :
    ViewModel() {

    private val _detailData = MutableLiveData<List<Any>>()
    val detailData: MutableLiveData<List<Any>> // why cannot use LiveData<List<Any>>
        get() = _detailData

    val navigateToInfo = MutableLiveData<Any>()

    init {

        when (theme.order) {
            1 -> {
                Log.d("Ming", "theme.order :${theme.order}")
                Log.d("Ming", "data: ${ActivityData.activity.activity}")
                _detailData.value = ActivityData.activity.activity
            }
            6 -> {
                _detailData.value = VenueData.venue.venue
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
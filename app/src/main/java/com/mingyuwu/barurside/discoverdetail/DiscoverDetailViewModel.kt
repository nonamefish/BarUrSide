package com.mingyuwu.barurside.discoverdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.mockdata.*
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.filter.FilterParameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        when (theme) {
            Theme.RECENT_ACTIVITY -> {
                _detailData.value = ActivityData.activity.activity
            }
            Theme.USER_ACTIVITY -> {
                _detailData.value = ActivityData.activity.activity
            }
            Theme.MAP_FILTER -> {
                if (filterParameter != null) {
                    getMapFilterResult(filterParameter)
                }
            }
            Theme.USER_FRIEND -> {
                _detailData.value = UserData.user.user
            }
            Theme.NOTIFICATION -> {
                _detailData.value = NotificationData.notification.notification
            }
            Theme.VENUE_MENU -> {
                _detailData.value = DrinkData.drink.drink
            }
            Theme.AROUND_VENUE -> {
                _detailData.value = VenueData.venue.venue
            }
            Theme.HOT_VENUE -> {
                _detailData.value = VenueData.venue.venue
            }
            Theme.HIGH_RATE_VENUE -> {
                _detailData.value = VenueData.venue.venue
            }
            Theme.HOT_DRINK -> {
                _detailData.value = DrinkData.drink.drink
            }
            Theme.HIGH_RATE_DRINK -> {
                _detailData.value = DrinkData.drink.drink
            }
        }
    }

    fun getMapFilterResult(filter: FilterParameter) {
        coroutineScope.launch {

            val result = repository.getVenueByFilter(filter)
            _detailData.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    null
                }
                else -> {
                    null
                }
            }

            Log.d("Ming","getMapFilterResult detailData: ${_detailData.value}")
            _detailData.value = _detailData.value
        }
    }
}
package com.mingyuwu.barurside.discoverdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.R
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
import kotlin.math.cos

class DiscoverDetailViewModel(
    val repository: BarUrSideRepository,
    val id: List<String>?,
    val theme: Theme,
    val filterParameter: FilterParameter?
) :
    ViewModel() {

    private val _detailData = MutableLiveData<List<Any>>()
    val detailData: MutableLiveData<List<Any>> // why cannot use LiveData<List<Any>>
        get() = _detailData

    val navigateToInfo = MutableLiveData<Any>()
    private lateinit var result: Result<Any>

    var mLocation = MutableLiveData<LatLng>()

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        coroutineScope.launch {

            when (theme) {
                Theme.NOTIFICATION -> {
                    _detailData.value = NotificationData.notification.notification
                }
                // Map Fragment
                Theme.MAP_FILTER -> {
                    if (filterParameter != null) {
                        result = repository.getVenueByFilter(filterParameter)
                    }
                }
                // Venue Info
                Theme.VENUE_MENU -> {
                    _detailData.value = DrinkData.drink.drink
                }
                // Profile Page
                Theme.USER_FRIEND -> {
                    id?.let{
                        result = repository.getFriend(id)
                    }
                }
                Theme.RECENT_ACTIVITY -> {
                    result = repository.getActivityResult()
                }
                Theme.USER_ACTIVITY -> {
                    id?.get(0)?.let{
                        result = repository.getActivityByUser(id[0])
                    }
                }
                // Discover Page
                Theme.AROUND_VENUE -> {
                    result = if (mLocation.value != null) {
                        val range = getRectangleRange(mLocation.value!!, 1.0)
                        repository.getVenueByLocation(range[0], range[1], range[2], range[3])
                    } else {
                        Result.Fail("hasn't get location information")
                    }
                }
                Theme.HOT_VENUE -> {
                    result = repository.getHotVenueResult()
                }
                Theme.HIGH_RATE_VENUE -> {
                    result = repository.getHighRateVenueResult()
                }
                Theme.HOT_DRINK -> {
                    result = repository.getHotDrinkResult()
                }
                Theme.HIGH_RATE_DRINK -> {
                    result = repository.getHighRateDrinkResult()
                }
            }

            if (::result.isInitialized) {
                _detailData.value = when (result) {
                    is Result.Success -> {
                        Log.d("Ming", "result:  ${(result as Result.Success<Any>).data}")
                        _error.value = null
                        (result as Result.Success<Any>).data as List<Any>
                    }
                    is Result.Fail -> {
                        _error.value = (result as Result.Fail).error
                        null
                    }
                    is Result.Error -> {
                        _error.value = (result as Result.Error).exception.toString()
                        null
                    }
                    else -> {
                        null
                    }
                }
            }
        }
    }

    private fun getRectangleRange(location: LatLng, distance: Double): List<Double> {
        //1緯度的距離大約為 69 英里 (111.11公里)
        //1經度的距離大約為 111.11 * cos(theta) km
        val minLat = location.latitude - (distance / 111.11)
        val maxLat = location.latitude + (distance / 111.11)
        val minLng = location.longitude - (distance / 111.11 / cos(location.latitude))
        val maxLng = location.longitude + (distance / 111.11 / cos(location.latitude))
        return listOf(minLat, maxLat, minLng, maxLng)
    }

    fun getAroundVenue(location: LatLng) {
        coroutineScope.launch {
            val range = getRectangleRange(location, 1.0)
            result = repository.getVenueByLocation(range[0], range[1], range[2], range[3])

            _detailData.value = when (result) {
                is Result.Success -> {
                    Log.d("Ming", "result:  ${(result as Result.Success<Any>).data.toString()}")
                    _error.value = null
                    (result as Result.Success<Any>).data as List<Any>
                }
                is Result.Fail -> {
                    _error.value = (result as Result.Fail).error
                    null
                }
                is Result.Error -> {
                    _error.value = (result as Result.Error).exception.toString()
                    null
                }
                else -> {
                    null
                }
            }

        }
    }

}
package com.mingyuwu.barurside.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.filter.FilterParameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.cos

class MapViewModel(private val repository: BarUrSideRepository) : ViewModel() {

    // set source data
    var drinkInfo = MutableLiveData<Drink>()
    var venueList = MutableLiveData<List<Venue>>()
    var searchInfo = MutableLiveData<List<Venue>>()
    var searchText = MutableLiveData<String?>()

    // navigate to venue
    val navigateToVenue = MutableLiveData<String?>()

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
    }

    fun getVenueBySearch(search: String) {
        coroutineScope.launch {
            val result = repository.getVenueBySearch(search)
            searchInfo.value = when (result) {
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
        }
    }

    fun getVenueByLocation(location: LatLng) {
        coroutineScope.launch {
            val range = getRectangleRange(location, 1.0)

            val result = repository.getVenueByLocation(range[0],range[1],range[2],range[3])
            venueList.value = when (result) {
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
}
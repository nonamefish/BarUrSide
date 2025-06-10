package com.mingyuwu.barurside.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.util.Util.getRectangleRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MapViewModel(private val repository: BarUrSideRepository) : ViewModel() {

    // set source data
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

            val result = repository.getVenueByLocation(range[0], range[1], range[2], range[3])
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

    fun onLeft() {
        navigateToVenue.value = null
    }
}

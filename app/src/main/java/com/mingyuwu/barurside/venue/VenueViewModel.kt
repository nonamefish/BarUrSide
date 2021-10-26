package com.mingyuwu.barurside.venue

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.mockdata.VenueData
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VenueViewModel(private val repository: BarUrSideRepository, id: String) : ViewModel() {

    //    var _venueInfo = MutableLiveData<Venue>()
//    val venueInfo: LiveData<Venue>
//        get() = _venueInfo
    var venueInfo = MutableLiveData<Venue>()

    //    private val _rtgInfo = MutableLiveData<List<Rating>>()
//    val rtgInfo: LiveData<List<Rating>>
//        get() = _rtgInfo
    var rtgInfo = MutableLiveData<List<Rating>>()


    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()


    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getVenueResult("1")
        getRatingResult("1", false)
    }

    private fun getVenueResult(id: String) {
        venueInfo = repository.getVenue(id)
    }

    private fun getRatingResult(id: String, isVenue: Boolean) {
        rtgInfo = repository.getRating(id, isVenue)
    }

}
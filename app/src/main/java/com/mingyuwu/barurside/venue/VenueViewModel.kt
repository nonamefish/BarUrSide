package com.mingyuwu.barurside.venue

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class VenueViewModel(private val repository: BarUrSideRepository, id: String) : ViewModel() {

    // set source data
    var venueInfo = MutableLiveData<Venue>()
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

    fun setImgs(rtgs: List<Rating>?): List<String> {
        var list = listOf<String>()
        rtgs?.forEach {
            if (list.size > 10) return@forEach
            list += it.images as List<String>
        }
        return list
    }
}
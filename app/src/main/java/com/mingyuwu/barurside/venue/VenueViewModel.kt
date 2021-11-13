package com.mingyuwu.barurside.venue

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Collect
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VenueViewModel(private val repository: BarUrSideRepository, val id: String) : ViewModel() {

    // set source data
    var venueInfo = MutableLiveData<Venue>()
    var rtgInfo = MutableLiveData<List<RatingInfo>>()
    var isCollect = MutableLiveData<Boolean?>()
    val userId = UserManager.user.value?.id ?: ""

    // navigate to all rating
    var navigateToAll = MutableLiveData<List<RatingInfo>?>()

    // image list data
    private val _images = MutableLiveData<List<String>?>()

    val images: LiveData<List<String>?>
        get() = _images

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getVenueResult(id)
        getRatingResult(id, true)
        getCollect(userId)
    }

    private fun getVenueResult(id: String) {
        venueInfo = repository.getVenue(id)
    }

    private fun getRatingResult(id: String, isVenue: Boolean) {
        rtgInfo = repository.getRatingByObject(id, isVenue)
    }

    private fun getCollect(userId: String) {
        coroutineScope.launch {
            val result = repository.getCollect(userId)
            isCollect.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    result.data.any { it.objectId == id }
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


    fun setImages(rtgs: List<RatingInfo>){
        var list = listOf<String>()
        rtgs?.forEach { ratingInfo ->
            ratingInfo.images?.let { imgs ->
                list += imgs
            }
        }
        _images.value = list
    }

    fun setCollect() {
        when (isCollect.value) {
            true -> {
                isCollect.value = false
                removeCollect(id,userId)
            }
            false -> {
                val postItem = Collect("", true, userId, id)
                postCollect(postItem)
                isCollect.value = true
            }
        }
    }

    private fun postCollect(collect: Collect) {
        coroutineScope.launch {
            val result = repository.postCollect(collect)
            when (result) {
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

    private fun removeCollect(id: String, userId: String) {
        coroutineScope.launch {
            val result = repository.removeCollect(id,userId)
            when (result) {
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

    fun navigateToAllRating() {
        navigateToAll.value = rtgInfo.value
    }

    fun onLeft() {
        navigateToAll.value = null
    }
}
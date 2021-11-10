package com.mingyuwu.barurside.drink

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DrinkViewModel(private val repository: BarUrSideRepository, val id: String) : ViewModel() {

    // set source data
    var drinkInfo = MutableLiveData<Drink>()
    var venueInfo = MutableLiveData<Venue>()
    var rtgInfo = MutableLiveData<List<RatingInfo>>()
    var isCollect = MutableLiveData<Boolean?>()
    val userId = UserManager.user.value?.id ?: ""

    // navigate to all rating
    var navigateToAll = MutableLiveData<List<RatingInfo>?>()

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getCollect(userId)
        getDrinkResult(id)
    }

    private fun getDrinkResult(id: String) {
        drinkInfo = repository.getDrink(id)
    }

    fun getRatingResult(id: String, isVenue: Boolean) {
        rtgInfo = repository.getRatingByObject(id, isVenue)
    }

    fun getVenueResult(id: String) {
        venueInfo = repository.getVenue(id)
        venueInfo.value = venueInfo.value
    }

    fun setImgs(rtgs: List<RatingInfo>?): List<String> {
        var list = listOf<String>()
        rtgs?.forEach {
            if (list.size > 10) return@forEach
            list += it.images as List<String>
        }
        return list
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

    fun setCollect() {
        when (isCollect.value) {
            true -> {
                isCollect.value = false
                removeCollect(id, userId)
            }
            false -> {
                val postItem = Collect("", false, userId, id)
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
            val result = repository.removeCollect(id, userId)
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
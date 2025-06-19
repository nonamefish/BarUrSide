package com.mingyuwu.barurside.venue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.mingyuwu.barurside.data.Collect
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.rating.ScoreStatus
import com.mingyuwu.barurside.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalTime

class VenueViewModel(private val repository: BarUrSideRepository, val id: String) : ViewModel() {

    // set source data
    val venueInfo = MutableLiveData<Venue>()
    var menuInfo = MutableLiveData<List<Drink>>()
    var rtgInfos = MutableLiveData<List<RatingInfo>>()
    var isCollect = MutableLiveData<Boolean?>()
    val userId = UserManager.user.value?.id ?: ""

    // navigate to all rating
    var navigateToAll = MutableLiveData<List<RatingInfo>?>()

    // navigate to menu
    var navigateToMenu = MutableLiveData<String?>()

    // images: The firebase MutableLiveData that stores the venue images
    private val _images = MutableLiveData<List<String>?>()

    val images: LiveData<List<String>?>
        get() = _images

    // status: The firebase MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadStatus>()

    val status: LiveData<LoadStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    // 星星顯示
    val starsList = venueInfo.map {
        val starList = mutableListOf<ScoreStatus>()
        val stars = it?.avgRating?.toInt() ?: 0
        repeat(stars) { starList += ScoreStatus.FULL }
        starList
    }

    // 營業狀態顯示
    val serviceStatus = venueInfo.map { venue ->
        val serviceTime = venue?.serviceTime
        if (serviceTime.isNullOrEmpty() || !serviceTime.contains("-")) return@map ""
        val open = serviceTime.split("-")[0]
        val close = serviceTime.split("-")[1]
        val current = LocalTime.now()
        return@map if (checkTime(open, close)) {
            if (open.split(":")[0].toInt() < close.split(":")[0].toInt()) {
                "營業中 直至$close"
            } else {
                "營業中 直至明天$close"
            }
        } else {
            if (current.isBefore(LocalTime.parse(open))) {
                "休息中，今日$open 開始營業"
            } else {
                "休息中，明天$open 開始營業"
            }
        }
    }

    init {
        getVenueResult(id)
        getMenu(id)
        getVenueRatingResult(id)
        getCollect(userId)
    }

    private fun getVenueResult(id: String) {
        repository.getVenue(id).observeForever { venue ->
            venueInfo.value = venue
        }
    }

    private fun getMenu(id: String) {
        coroutineScope.launch {
            val result = repository.getMenu(id)
            menuInfo.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    null
                }
                else -> {
                    null
                }
            }
        }
    }

    private fun getVenueRatingResult(id: String) {
        rtgInfos = repository.getRatingByObject(id, true)
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

    fun setImages(rtgs: List<RatingInfo>) {

        val list = mutableListOf<String>()

        rtgs.forEach { ratingInfo ->
            ratingInfo.images?.let { images ->
                list += images
            }
        }

        _images.value = list
    }

    fun setCollect() {
        when (isCollect.value) {
            true -> {
                isCollect.value = false
                removeCollect(id, userId)
            }
            false -> {
                val postItem = Collect("", true, userId, id)
                addCollect(postItem)
                isCollect.value = true
            }
            else -> {}
        }
    }

    private fun addCollect(collect: Collect) {
        coroutineScope.launch {
            val result = repository.addCollect(collect)
            when (result) {
                is Result.Success -> {
                    _error.value = null
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                }
                else -> {
                    Logger.w("Wrong Result Type: $result")
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
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                }
                else -> {
                    Logger.w("Wrong Result Type: $result")
                }
            }
        }
    }

    fun navigateToMenu() {
        venueInfo.value?.let {
            navigateToMenu.value = venueInfo.value!!.id
        }
    }

    fun navigateToAllRating() {
        navigateToAll.value = rtgInfos.value
    }

    fun onLeft() {
        navigateToAll.value = null
        navigateToMenu.value = null
    }

    private fun checkTime(open: String, close: String): Boolean {
        val openTime = LocalTime.parse(open)
        val closeTime = LocalTime.parse(close)
        val current = LocalTime.now()
        return current.isAfter(openTime) && current.isBefore(closeTime)
    }
}

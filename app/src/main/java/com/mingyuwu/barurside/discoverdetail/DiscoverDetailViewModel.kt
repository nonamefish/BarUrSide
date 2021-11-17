package com.mingyuwu.barurside.discoverdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.filter.FilterParameter
import com.mingyuwu.barurside.login.UserManager
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

    private var _detailData = MutableLiveData<List<Any>>()
    val detailData: MutableLiveData<List<Any>> // why cannot use LiveData<List<Any>>
        get() = _detailData

    val navigateToInfo = MutableLiveData<Any?>()
    private lateinit var result: Result<Any?>

    var mLocation = MutableLiveData<LatLng>()

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // status: The firebase MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadStatus>()

    val status: LiveData<LoadStatus>
        get() = _status

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        // snapshot listener
        _detailData = when (theme) {
            Theme.RECENT_ACTIVITY -> {
                repository.getActivityResult() as MutableLiveData<List<Any>>
            }
            Theme.NOTIFICATION -> {
                repository.getNotification(
                    UserManager.user.value?.id ?: ""
                ) as MutableLiveData<List<Any>>
            }
            else -> _detailData
        }

        coroutineScope.launch {

            when (theme) {
                // Map Fragment
                Theme.MAP_FILTER -> {
                    if (filterParameter != null) {
                        result = repository.getVenueByFilter(filterParameter)
                    }
                }
                // Venue Info
                Theme.VENUE_MENU -> {
                    id?.get(0)?.let {
                        result = repository.getMenu(id[0])
                    }
                }
                // Profile Page
                Theme.USER_FRIEND -> {
                    if (id != null) {
                        result = repository.getFriend(id)
                    } else {
                        result = Result.Success(null)
                    }
                }
                Theme.USER_ACTIVITY -> {
                    id?.get(0)?.let {
                        result = repository.getActivityByUser(id[0])
                    }
                }
                // Discover Page
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
                        _error.value = null
                        _status.value = LoadStatus.DONE
                        (result as Result.Success<Any>).data as List<Any>
                    }
                    is Result.Fail -> {
                        _error.value = (result as Result.Fail).error
                        _status.value = LoadStatus.ERROR
                        null
                    }
                    is Result.Error -> {
                        _error.value = (result as Result.Error).exception.toString()
                        _status.value = LoadStatus.ERROR
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
                    _status.value = LoadStatus.DONE
                    (result as Result.Success<Any>).data as List<Any>
                }
                is Result.Fail -> {
                    _error.value = (result as Result.Fail).error
                    _status.value = LoadStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = (result as Result.Error).exception.toString()
                    _status.value = LoadStatus.ERROR
                    null
                }
                else -> {
                    null
                }
            }

        }
    }

    fun replyAddFriend(notify: Notification, reply: Boolean) {
        coroutineScope.launch {
            notify.reply = reply
            result = repository.replyAddFriend(notify, reply)
            when (result) {
                is Result.Success -> {
                    _error.value = null
                    Log.d("Ming", "result: $(result as Result.Success<Any>).data}")
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

    fun onLeft() {
        navigateToInfo.value = null
    }

    fun checkNotification(ids: List<String>){
        coroutineScope.launch {
            result = repository.checkNotification(ids)
            when (result) {
                is Result.Success -> {
                    _error.value = null
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
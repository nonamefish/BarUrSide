package com.mingyuwu.barurside.discoverdetail

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
import com.mingyuwu.barurside.util.Logger
import com.mingyuwu.barurside.util.Util.getRectangleRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DiscoverDetailViewModel(
    val repository: BarUrSideRepository,
    val id: List<String>?,
    val theme: Theme,
    val filterParameter: FilterParameter?
) :
    ViewModel() {

    var location = MutableLiveData<LatLng>()

    // detailData: The internal MutableLiveData that stores the discover theme list date
    private var _detailData = MutableLiveData<List<Any>>()
    val detailData: MutableLiveData<List<Any>> // why cannot use LiveData<List<Any>>
        get() = _detailData

    // navigate to object info. including activity, venue and drink
    val navigateToInfo = MutableLiveData<Any?>()
    private lateinit var result: Result<Any?>

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

                // map Fragment
                Theme.MAP_FILTER -> {
                    if (filterParameter != null) {
                        result = repository.getVenueByFilter(filterParameter)
                    }
                }
                // venue Info
                Theme.VENUE_MENU -> {
                    id?.get(0)?.let {
                        result = repository.getMenu(id[0])
                    }
                }
                // profile Page
                Theme.USER_FRIEND -> {
                    result = if (id != null) {
                        repository.getUsersResult(id)
                    } else {
                        Result.Success(null)
                    }
                }
                // activity user booked
                Theme.USER_ACTIVITY -> {
                    id?.get(0)?.let {
                        result = repository.getActivityByUser(id[0])
                    }
                }
                // discover Page
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
                else -> {
                    Logger.d("DiscoverDetailViewModel Other Theme: $theme")
                }
            }

            if (::result.isInitialized) {
                _detailData.value = when (result) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadStatus.DONE
                        (result as Result.Success<*>).data as List<Any>?
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

    fun getAroundVenue(location: LatLng) {

        coroutineScope.launch {

            val range = getRectangleRange(location, 1.0)
            result = repository.getVenueByLocation(range[0], range[1], range[2], range[3])

            _detailData.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    (result as Result.Success<*>).data as List<Any>?
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
                else -> null
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
                }
                is Result.Fail -> {
                    _error.value = (result as Result.Fail).error
                }
                is Result.Error -> {
                    _error.value = (result as Result.Error).exception.toString()
                }
                else -> {
                    Logger.w("Wrong Result Type: $result")
                }
            }
        }
    }

    fun checkNotification(ids: List<String>) {
        coroutineScope.launch {

            result = repository.checkNotification(ids)
            when (result) {
                is Result.Success -> {
                    _error.value = null
                }
                is Result.Fail -> {
                    _error.value = (result as Result.Fail).error
                }
                is Result.Error -> {
                    _error.value = (result as Result.Error).exception.toString()
                }
                else -> {
                    Logger.w("Wrong Result Type: $result")
                }
            }
        }
    }

    fun onLeft() {
        navigateToInfo.value = null
    }
}

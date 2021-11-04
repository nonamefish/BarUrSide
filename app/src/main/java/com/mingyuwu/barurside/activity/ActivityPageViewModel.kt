package com.mingyuwu.barurside.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.mockdata.*
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.rating.InfoRatingAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ActivityPageViewModel(
    private val repository: BarUrSideRepository,
    val type: ActivityTypeFilter
) : ViewModel() {


    private val userId = UserManager.user.value!!.id

    private var _rtgData = MutableLiveData<List<Any>>()
    val rtgData : LiveData<List<Any>>
        get() = _rtgData

    // navigate to activity detail
    val navigateToDetail = MutableLiveData<Any?>()

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        when (type) {
            ActivityTypeFilter.RECOMMEND -> {
                getRatingByRecommend()
            }
            ActivityTypeFilter.ACTIVITY -> {
                getRecentActivity()
            }
            ActivityTypeFilter.FOLLOW -> {
                getRatingByFriend(userId)
            }
        }
    }

    private fun getRecentActivity() {
        _rtgData = repository.getActivityResult() as MutableLiveData<List<Any>>
    }

    private fun getRatingByRecommend() {
        coroutineScope.launch {
            val result = repository.getRatingByRecommend()
            _rtgData.value = when (result) {
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

    private fun getRatingByFriend(userId: String) {
        coroutineScope.launch {
            val result = repository.getRatingByFriends(userId)
            _rtgData.value = when (result) {
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

}
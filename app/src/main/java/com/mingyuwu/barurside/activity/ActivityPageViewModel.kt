package com.mingyuwu.barurside.activity

import android.util.Log
import androidx.lifecycle.*
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.login.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ActivityPageViewModel(
    private val repository: BarUrSideRepository,
    val type: ActivityTypeFilter
) : ViewModel() {


    val user = UserManager.user

    private var _listDate = MutableLiveData<List<Any>>()
    val listDate : LiveData<List<Any>>
        get() = _listDate

    // navigate to activity detail
    val navigateToDetail = MutableLiveData<Any?>()

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
        when (type) {
            ActivityTypeFilter.RECOMMEND -> {
                getRatingByRecommend()
            }
            ActivityTypeFilter.ACTIVITY -> {
                getRecentActivity()
                _status.value = LoadStatus.DONE
            }
            ActivityTypeFilter.FOLLOW -> {
                user.value?.let{
                    getRatingByFriend(true, it.id)
                }
            }
        }
    }

    private fun getRecentActivity() {
        _listDate = repository.getActivityResult() as MutableLiveData<List<Any>>
        _status.value = LoadStatus.DONE
    }

    private fun getRatingByRecommend(isInitial: Boolean = false) {

        if (isInitial) _status.value = LoadStatus.LOADING

        coroutineScope.launch {
            val result = repository.getRatingByRecommend()
            _listDate.value = when (result) {
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

    fun getRatingByFriend(isInitial: Boolean = false, userId: String) {

        if (isInitial) _status.value = LoadStatus.LOADING

        coroutineScope.launch {
            val result = repository.getRatingByFriends(userId)
            _listDate.value = when (result) {
                is Result.Success -> {
                    Log.d("Ming","result.data: ${result.data}")
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    Log.d("Ming","result.data: Fail")
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    null
                }
                is Result.Error -> {
                    Log.d("Ming","result.data: Error")
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

    fun onLeft(){
        navigateToDetail.value = null
    }

}
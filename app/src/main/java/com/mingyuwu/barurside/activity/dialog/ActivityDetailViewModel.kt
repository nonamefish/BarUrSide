package com.mingyuwu.barurside.activity.dialog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.util.Util
import com.mingyuwu.barurside.util.Util.calculateDateByPeriod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ActivityDetailViewModel(
    private val repository: BarUrSideRepository,
    activity: Activity?,
    val activityId: String?
) : ViewModel() {

    private val userId = UserManager.user.value?.id!!

    var dtActivity = MutableLiveData<Activity>()
    var isBook = MutableLiveData<Boolean>()

    // navigate to activity detail
    var navigateToDetail = MutableLiveData<Any?>()

    // error: The internal MutableLiveData that stores the error of the most recent request
    private var _sponsor = MutableLiveData<User>()

    val sponsor: LiveData<User?>
        get() = _sponsor

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
        if (activityId != null) {
            getActivity(activityId)
        } else {
            dtActivity.value = activity ?: null
            checkUserIsBook()
            getSponsorData()
        }

    }

    fun modifyActivity() {
        dtActivity.value?.let{
            coroutineScope.launch {
                repository.modifyActivity(it.id, userId)
                navigateToDetail.value = true
            }
        }
    }

    fun bookActivity() {
        dtActivity.value?.let{
            coroutineScope.launch {
                val notification = Notification(
                    "",
                    "activity",
                    "",
                    "activity",
                    calculateDateByPeriod(it.startTimestamp!!, "DAY", -1),
                    it.id,
                    userId,
                    "提醒：今日你有一個即將舉行的活動 <b>${it.name}</b> ",
                    null,
                    false
                )
                repository.bookActivity(it.id, userId, notification)
                navigateToDetail.value = true
            }
        }
    }

    private fun getSponsorData() {
        dtActivity.value?.let {
            _sponsor = repository.getUser(it.sponsor)
        }
    }

    private fun checkUserIsBook(){
        dtActivity.value?.let{
            isBook.value = it.bookers!!.any { it.id == userId }
        }
    }

    fun onLeft() {
        navigateToDetail.value = null
    }

    private fun getActivity(activityId: String){
        coroutineScope.launch {
            val result = repository.getActivityById(activityId)
            dtActivity.value = when (result) {
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
            getSponsorData()
            checkUserIsBook()
        }
    }
}
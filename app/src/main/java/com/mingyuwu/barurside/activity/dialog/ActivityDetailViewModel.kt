package com.mingyuwu.barurside.activity.dialog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ActivityDetailViewModel(
    private val repository: BarUrSideRepository,
    val activity: Activity
) : ViewModel() {

    private val userId = UserManager.user.value?.id!!
    val isBook = activity.bookers!!.any { it.id == userId }

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

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        getSponsorData()
    }

    fun modifyActivity() {
        coroutineScope.launch {
            repository.modifyActivity(activity.id, userId)
            navigateToDetail.value = true
        }
    }

    fun bookActivity() {
        coroutineScope.launch {
            val notification = Notification(
                "",
                "activity",
                "",
                "activity",
                activity.startTime,
                activity.id,
                userId,
                "提醒：今日你有一個即將舉行的活動 <b>${activity.name}</b> ",
                null,
                false
            )
            repository.bookActivity(activity.id, userId, notification)
            navigateToDetail.value = true
        }
    }

    private fun getSponsorData() {
        _sponsor = repository.getUser(activity.sponsor)
    }

    fun onLeft() {
        navigateToDetail.value = null
    }
}
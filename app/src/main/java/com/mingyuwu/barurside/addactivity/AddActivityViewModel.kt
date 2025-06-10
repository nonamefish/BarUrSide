package com.mingyuwu.barurside.addactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.data.Relationship
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.util.DateUnit
import com.mingyuwu.barurside.util.Util.calculateDateByPeriod
import com.mingyuwu.barurside.util.Util.convertStringToTimestamp
import com.mingyuwu.barurside.util.Util.getString
import java.sql.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddActivityViewModel(private val repository: BarUrSideRepository) : ViewModel() {

    // activity information
    val name = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val limit = MutableLiveData<String>()
    val mainDrink = MutableLiveData<String>()
    val startTime = MutableLiveData<String>()
    val endTime = MutableLiveData<String>()
    val userId = UserManager.user.value?.id!!

    // navigate to home
    val navigateToDetail = MutableLiveData<Boolean?>()

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun postActivity() {

        coroutineScope.launch {

            val activity = Activity(
                "",
                name.value!!,
                convertStringToTimestamp(startTime.value!!),
                convertStringToTimestamp(endTime.value!!),
                address.value!!,
                limit.value!!.toLong(),
                mainDrink.value!!,
                userId,
                listOf(Relationship(userId, Timestamp(System.currentTimeMillis())))
            )

            val notification = Notification(
                "",
                getString(R.string.activity),
                "",
                getString(R.string.activity),
                calculateDateByPeriod(
                    convertStringToTimestamp(startTime.value!!),
                    DateUnit.DAY,
                    -1
                ),
                "",
                userId,
                getString(R.string.activity_notify, name.value ?: ""),
                null,
                false
            )

            repository.postActivity(activity, notification)
            navigateToDetail.value = true
        }
    }

    fun onLeft() {
        navigateToDetail.value = null
    }

    fun checkValueCompleted(): Boolean {
        if (name.value == null ||
            startTime.value == null ||
            endTime.value == null ||
            address.value == null ||
            limit.value == null ||
            mainDrink.value == null
        ) {
            return false
        }
        return true
    }

    fun checkTimeRange(): Boolean {
        // convert time from string to time(Long) then check endTime is greater than startTime
        val startTime = convertStringToTimestamp(startTime.value!!).time
        val endTime = convertStringToTimestamp(endTime.value!!).time
        return startTime < endTime
    }
}

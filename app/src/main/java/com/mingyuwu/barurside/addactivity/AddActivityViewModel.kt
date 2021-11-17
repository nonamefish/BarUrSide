package com.mingyuwu.barurside.addactivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.data.Relationship
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

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


    private fun convertStringToTimestamp(time: String): Timestamp {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd  a HH:mm", Locale.TAIWAN)
        val parsedDate = dateFormat.parse(time)
        return Timestamp(parsedDate.time)
    }


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
                "activity",
                "",
                "activity",
                convertStringToTimestamp(startTime.value!!),
                "",
                userId,
                "提醒：今日你有一個即將舉行的活動 <b>${name.value!!}</b>",
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

    fun checkValue(): Boolean {
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
}
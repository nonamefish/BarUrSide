package com.mingyuwu.barurside.activity.dialog

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

class ActivityDetailViewModel(
    private val repository: BarUrSideRepository,
) : ViewModel() {

    private val userId = UserManager.user.value!!.id

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



    fun modifyActivity(activityId: String,useId: String) {
        coroutineScope.launch {
            repository.modifyActivity(activityId,userId)
        }
    }

    fun bookActivity(activityId: String,useId: String) {
        coroutineScope.launch {
            repository.modifyActivity(activityId,userId)
        }
    }

}
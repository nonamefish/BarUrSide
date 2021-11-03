package com.mingyuwu.barurside.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.mockdata.*
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.rating.InfoRatingAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ActivityPageViewModel(
    private val repository: BarUrSideRepository,
    val type: ActivityTypeFilter
) : ViewModel() {

    private val _activityData = MutableLiveData<List<Any>>()
    val activityData: MutableLiveData<List<Any>> // why cannot use LiveData<List<Any>>
        get() = _activityData

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
                _activityData.value = RatingInfoData.rating.rating
            }
            ActivityTypeFilter.ACTIVITY -> {
                getRecentActivity()
            }
            ActivityTypeFilter.FOLLOW -> {
                _activityData.value = RatingInfoData.rating.rating
            }
        }
    }

    private fun getRecentActivity() {
        coroutineScope.launch {
            val result = repository.getActivityResult()
            activityData.value = when (result) {
                is Result.Success -> {
                    Log.d("Ming", "result:  ${(result as Result.Success<Any>).data}")
                    _error.value = null
                    (result as Result.Success<Any>).data as List<Any>
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
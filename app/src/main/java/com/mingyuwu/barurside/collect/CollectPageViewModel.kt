package com.mingyuwu.barurside.collect

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Collect
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CollectPageViewModel(val repository: BarUrSideRepository, val isVenue: Boolean) :
    ViewModel() {

    // set source data
    var collectInfo = MutableLiveData<List<Collect>>()
    val navigateToObject = MutableLiveData<String?>()
    val userId = "6BhbnIMi1Ai91Ky4w9rI"

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getCollect(userId)
    }

    private fun getCollect(userId: String) {
        coroutineScope.launch {
            val result = repository.getCollect(userId)
            collectInfo.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    Log.d("Ming", "result.data: ${result.data}")
                    result.data.filter { it.isVenue == isVenue }
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

    fun setNavigateToObject(id: String) {
        navigateToObject.value = id
    }

    fun onLeft() {
        navigateToObject.value = null
    }
}
package com.mingyuwu.barurside.discover

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DiscoverViewModel(private val repository: BarUrSideRepository) : ViewModel() {

    var searchType = MutableLiveData(false) // search type for venue(store) or drink
    var searchText = MutableLiveData<String?>() // search content user written
    var searchInfo = MutableLiveData<List<*>?>() // info after user set search on editText

    // navigate to object info page
    private var _navigateToObject = MutableLiveData<String?>()
    val navigateToObject: LiveData<String?>
        get() = _navigateToObject

    // navigate to theme
    private var _navigateToTheme = MutableLiveData<Theme?>()
    val navigateToTheme: LiveData<Theme?>
        get() = _navigateToTheme

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getVenueBySearch(search: String) {

        coroutineScope.launch {

            val result = repository.getVenueBySearch(search)
            searchInfo.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    Log.d("Ming", "result.data: ${result.data}")
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

    fun getDrinkBySearch(search: String) {

        coroutineScope.launch {

            val result = repository.getDrinkBySearch(search)
            searchInfo.value = when (result) {
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

    fun navigateToObject(id: String) {
        _navigateToObject.value = id
    }

    fun navigateToTheme(theme: Theme) {
        _navigateToTheme.value = theme
    }

    fun onLeft() {
        _navigateToObject.value = null
        _navigateToTheme.value = null
    }
}

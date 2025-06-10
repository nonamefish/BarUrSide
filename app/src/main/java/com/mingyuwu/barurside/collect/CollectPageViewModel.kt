package com.mingyuwu.barurside.collect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.data.Collect
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CollectPageViewModel(val repository: BarUrSideRepository, val isVenue: Boolean) :
    ViewModel() {

    // set source data
    var collectInfo = MutableLiveData<List<Collect>>()
    var objectInfo = MutableLiveData<List<Any>>()
    var isCollect = MutableLiveData<MutableList<Boolean>?>()
    val navigateToObject = MutableLiveData<String?>()
    val userId = UserManager.user.value?.id ?: ""
    var location = MutableLiveData<LatLng>()

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
        getCollect(userId)
    }

    private fun getCollect(userId: String) {

        coroutineScope.launch {

            val result = repository.getCollect(userId)
            collectInfo.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    result.data.filter { it.isVenue == isVenue }
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

    fun getObjectInfo(isVenue: Boolean, collectInfo: List<Collect>) {

        coroutineScope.launch {

            val result = when (isVenue) {
                true -> repository.getVenueByIds(collectInfo.map { it.objectId })
                false -> repository.getDrinksByIds(collectInfo.map { it.objectId })
            }

            isCollect.value = collectInfo.map { true }.toMutableList()

            objectInfo.value = when (result) {
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

    fun setCollect(id: String, position: Int, isVenue: Boolean) {

        isCollect.value?.let {
            when (it[position]) {
                true -> {
                    it[position] = false
                    removeCollect(id, userId)
                }
                false -> {
                    val postItem = Collect("", isVenue, userId, id)
                    addCollect(postItem)
                    it[position] = true
                }
            }

            isCollect.value = isCollect.value
        }
    }

    private fun addCollect(collect: Collect) {

        coroutineScope.launch {

            val result = repository.addCollect(collect)
            when (result) {
                is Result.Success -> {
                    _error.value = null
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                }
                else -> {
                    Logger.d("Unknoen Result type: $result")
                }
            }
        }
    }

    private fun removeCollect(id: String, userId: String) {

        coroutineScope.launch {

            val result = repository.removeCollect(id, userId)
            when (result) {
                is Result.Success -> {
                    _error.value = null
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                }
                else -> {
                    Logger.d("Unknoen Result type: $result")
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

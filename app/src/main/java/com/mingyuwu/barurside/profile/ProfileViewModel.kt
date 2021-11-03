package com.mingyuwu.barurside.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Collect
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ProfileViewModel(private val repository: BarUrSideRepository, val userId: String) :
    ViewModel() {

    // set source data
    var userInfo = MutableLiveData<User>()
    var rtgInfo = MutableLiveData<List<RatingInfo>>()
    var isMyself = userId == UserManager.user.value!!.id
    val userId_test = "6BhbnIMi1Ai91Ky4w9rI"

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getUserInfo(userId)
        getRatingInfo(userId)
        Log.d("Ming","userId: $userId , myself: ${UserManager.user.value!!.id}")
        Log.d("Ming","isMyself: $isMyself")
    }

    private fun getUserInfo(userId: String) {
        userInfo = repository.getUser(userId)
    }

    private fun getRatingInfo(userId: String) {
        coroutineScope.launch {
            Log.d("Ming","getRatingInfo: $userId")
            val result = repository.getRatingByUser(userId)
            rtgInfo.value = when (result) {
                is Result.Success -> {
                    Log.d("Ming","user rating: ${result.data}")
                    _error.value = null
                    result.data
                }
                is Result.Fail -> {
                    Log.d("Ming","user rating: ${result.error}")
                    _error.value = result.error
                    null
                }
                is Result.Error -> {
                    Log.d("Ming","user rating: ${result.exception}")
                    _error.value = result.exception.toString()
                    null
                }
                else -> {
                    null
                }
            }
        }
    }

    fun setImgs(rtgs: List<RatingInfo>?): List<String> {
        var list = listOf<String>()
        rtgs?.forEach {
            if (list.size > 10) return@forEach
            list += it.images as List<String>
        }
        return list
    }

}
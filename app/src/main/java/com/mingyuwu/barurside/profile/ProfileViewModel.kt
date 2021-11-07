package com.mingyuwu.barurside.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp


class ProfileViewModel(private val repository: BarUrSideRepository, val userId: String) :
    ViewModel() {

    // set source data
    var userInfo = MutableLiveData<User>()
    private var _rtgInfo = MutableLiveData<List<RatingInfo>>()
    val rtgInfo: LiveData<List<RatingInfo>>
        get() = _rtgInfo

    var isMyself = userId == UserManager.user.value?.id
    var isFriend = UserManager.user.value?.friends?.any { it.id == userId }

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

    }

    private fun getUserInfo(userId: String) {
        userInfo = repository.getUser(userId)
    }

    private fun getRatingInfo(userId: String) {
        coroutineScope.launch {
            val result = repository.getRatingByUser(userId)
            _rtgInfo.value = when (result) {
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

    fun setImgs(rtgs: List<RatingInfo>?): List<String> {
        var list = listOf<String>()
        rtgs?.forEach {
            if (list.size > 10) return@forEach
            list += it.images as List<String>
        }
        return list
    }

    fun addOnFriend() {
        val notification = Notification(
            "",
            "profile",
            UserManager.user.value?.image ?: "",
            "friend",
            Timestamp(System.currentTimeMillis()),
            UserManager.user.value?.id ?: "",
            userId,
            "<b>${UserManager.user.value?.name}</b>想要加你好友",
            null
        )

        coroutineScope.launch {
            val result = repository.addFriend(notification)
            when (result) {
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
}
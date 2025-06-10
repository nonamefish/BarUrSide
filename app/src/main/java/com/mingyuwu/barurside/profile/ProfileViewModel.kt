package com.mingyuwu.barurside.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.data.source.LoadStatus
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.util.Logger
import com.mingyuwu.barurside.util.Util.getString
import java.sql.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: BarUrSideRepository, val userId: String) :
    ViewModel() {

    // set source data
    var userInfo = MutableLiveData<User>()
    var notifications = MutableLiveData<List<Notification>>()

    private var _rtgInfos = MutableLiveData<List<RatingInfo>>()
    val rtgInfos: LiveData<List<RatingInfo>>
        get() = _rtgInfos

    var isMyself = userId == UserManager.user.value?.id
    var isFriend =
        MutableLiveData<Boolean>(UserManager.user.value?.friends?.any { it.id == userId })

    // navigate to all rating
    var navigateToAll = MutableLiveData<List<RatingInfo>?>()

    // image list data
    private val _images = MutableLiveData<List<String>?>()

    val images: LiveData<List<String>?>
        get() = _images

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
        getUserInfo(userId)
        getRatingInfo(userId)
        getNotification(UserManager.user.value?.id)
    }

    private fun getNotification(userId: String?) {
        userId?.let {
            notifications = repository.getNotification(it)
        }
    }

    private fun getUserInfo(userId: String) {
        userInfo = repository.getUser(userId)
    }

    private fun getRatingInfo(userId: String) {
        coroutineScope.launch {
            val result = repository.getRatingByUser(userId)
            _rtgInfos.value = when (result) {
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

    fun setImages(rtgs: List<RatingInfo>) {

        val list = mutableListOf<String>()

        rtgs.forEach { ratingInfo ->
            ratingInfo.images?.let { images ->
                list += images
            }
        }

        _images.value = list
    }

    fun addOnFriend() {
        val notification = Notification(
            "",
            getString(R.string.profile),
            UserManager.user.value?.image ?: "",
            getString(R.string.friend),
            Timestamp(System.currentTimeMillis()),
            UserManager.user.value?.id ?: "",
            userId,
            getString(
                R.string.add_friend_notify,
                UserManager.user.value?.name ?: ""
            ),
            null,
            false
        )

        coroutineScope.launch {
            val result = repository.addFriend(notification)
            when (result) {
                is Result.Success -> {
                    _error.value = null
                }
                is Result.Fail -> {
                    _error.value = result.error
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                }
                else -> {
                    Logger.w("Wrong Result Type: $result")
                }
            }
        }
    }

    fun unfriendUser() {
        userId.let {
            coroutineScope.launch {
                val result = repository.unfriend(listOf(UserManager.user.value!!.id, userId))
                when (result) {
                    is Result.Success -> {
                        _error.value = null
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                    }
                    else -> {
                        Logger.w("Wrong Result Type: $result")
                    }
                }
            }
        }
    }

    fun navigateToAllRating(isVenue: Boolean) {
        if (isVenue) {
            navigateToAll.value = rtgInfos.value?.filter { it.isVenue == true }
        } else {
            navigateToAll.value = rtgInfos.value?.filter { it.isVenue == false }
        }
    }

    fun onLeft() {
        navigateToAll.value = null
    }
}

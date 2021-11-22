package com.mingyuwu.barurside

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.util.CurrentFragmentType
import com.mingyuwu.barurside.util.Util.getDiffHour
import kotlin.math.abs

class MainViewModel(private val repository: BarUrSideRepository) : ViewModel() {

    var notification = MutableLiveData<List<Notification>>()
    var notificationSize: LiveData<Int>? = null

    val navigateToStart = MutableLiveData<Boolean?>()
    val navigateToLogin = MutableLiveData<Boolean?>()
    val discoverType = MutableLiveData<String?>()


    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    fun onLeft() {
        navigateToStart.value = null
        navigateToLogin.value = null
    }

    fun getUserData(userId: String) {
        UserManager.user = repository.getUser(userId)
    }

    fun getNotification(userId: String) {
        notification = repository.getNotification(userId)

        // set notification size
        notificationSize = Transformations.map(notification) { it ->
            it.filter {
                it.toId == UserManager.user.value!!.id &&
                        (it.isCheck == false && (it.type == "friend" ||
                                (it.type == "activity" && getDiffHour(it.timestamp!!) < 24)))
            }.size
        }
    }

}
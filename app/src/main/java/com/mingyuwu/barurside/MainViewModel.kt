package com.mingyuwu.barurside

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.util.CurrentFragmentType

class MainViewModel(private val repository: BarUrSideRepository) : ViewModel() {

    var location = MutableLiveData<LatLng>()
    var notification = MutableLiveData<List<Notification>>()
    val navigateToStart = MutableLiveData<Boolean?>()
    val navigateToLogin = MutableLiveData<Boolean?>()

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
    }

}
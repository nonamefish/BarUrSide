package com.mingyuwu.barurside

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.login.UserManager

class MainViewModel : ViewModel() {

    var location = MutableLiveData<LatLng>()
    val navigateToStart = MutableLiveData<Boolean?>()
    val navigateToLogin = MutableLiveData<Boolean?>()

    fun onLeft() {
        navigateToStart.value = null
        navigateToLogin.value = null
    }

}
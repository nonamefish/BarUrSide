package com.mingyuwu.barurside.addactivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddActivityViewModel : ViewModel() {

    val name = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val limit = MutableLiveData<String>()
    val mainDrink = MutableLiveData<String>()
    val startTime = MutableLiveData<String>()
    val endTime = MutableLiveData<String>()

    fun convertStringToTimestamp(time: String){

    }
}
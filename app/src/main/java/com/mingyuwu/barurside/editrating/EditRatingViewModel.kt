package com.mingyuwu.barurside.editrating

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditRatingViewModel() : ViewModel() {

    private val _star = MutableLiveData<MutableList<Int>>()
    val star: LiveData<MutableList<Int>>
        get() = _star

    private val _a = MutableLiveData<Int>()
    val a: LiveData<Int>
        get() = _a

    private val _comment = MutableLiveData<MutableList<String>>()
    val comment: LiveData<MutableList<String>>
        get() = _comment

    private val _rtgOrder = MutableLiveData<Int>(0)
    val rtgOrder: LiveData<Int>
        get() = _rtgOrder

    fun clickRatingStore(score: Int, rtgOrder: Int) {
        if (_star.value == null) {
            _star.value = listOf(1).toMutableList()
        } else {
            _star.value!![rtgOrder] = score
            _star.value = _star.value
        }

        _a.value = score
        Log.d("Ming", "_star: ${star.value}")
    }

    fun addNewRating() {
        _rtgOrder.value = _rtgOrder.value?.plus(1)
    }

}
package com.mingyuwu.barurside.rating.all

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.RatingInfo

class AllRatingViewModel(val data: List<RatingInfo>) : ViewModel() {

    private val _ratings = MutableLiveData<List<RatingInfo>>()
    val numSelected = MutableLiveData<Int?>()

    val ratings: LiveData<List<RatingInfo>>
        get() = _ratings

    init {
        _ratings.value = data
    }

    fun getSpecificStarComment(star: Int) {
        _ratings.value = data.filter { it.rating == star.toLong() }
        numSelected.value = star
    }

    fun getSpecificStarCount(star: Int): Int {
        return data.filter { it.rating == star.toLong() }.size
    }
}

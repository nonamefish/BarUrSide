package com.mingyuwu.barurside.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.util.Style

class FilterViewModel() : ViewModel() {

    // filter input
    val navigateToResult = MutableLiveData<FilterParameter?>()
    val choiceLevel = MutableLiveData<Int?>()
    val choiceCategory = MutableLiveData<List<String>?>()
    val choiceStyle = MutableLiveData<List<String>?>()
    val choiceDistance = MutableLiveData<Double?>()

    fun navigateToResult() {
        navigateToResult.value = FilterParameter(
            choiceLevel.value,
            choiceCategory.value,
            if (choiceStyle.value.isNullOrEmpty()) {
                Style.entries.map { it.name }
            } else {
                choiceStyle.value
            },
            choiceDistance.value
        )
    }

    fun onLeft() {
        navigateToResult.value = null
    }

    fun checkValue(): Boolean {

        if (choiceLevel.value == null
        ) {
            return false
        }

        return true
    }
}

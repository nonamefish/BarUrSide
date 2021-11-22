package com.mingyuwu.barurside.filter


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.util.Style

class FilterViewModel() : ViewModel() {

    val navigateToResult = MutableLiveData<FilterParameter?>()
    val choiceLevel = MutableLiveData<Int?>()
    val choiceCategory = MutableLiveData<List<String>?>()
    val choiceStyle = MutableLiveData<List<String>?>()
    val choiceDistance = MutableLiveData<Double?>()


    fun navigateToResult() {
        navigateToResult.value = FilterParameter(
            choiceLevel.value ?: null,
            choiceCategory.value ?: null,
            if (choiceStyle.value.isNullOrEmpty()) {
                Style.values().map { it.name }
            } else {
                choiceStyle.value
            },
            choiceDistance.value ?: null
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
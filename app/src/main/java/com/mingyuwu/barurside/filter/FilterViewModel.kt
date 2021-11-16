package com.mingyuwu.barurside.filter


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilterViewModel() : ViewModel() {

    val navigateToResult = MutableLiveData<FilterParameter?>()
    val choiceLevel = MutableLiveData<Int?>()
    val choiceCategory = MutableLiveData<List<String>?>()
    val choiceStyle = MutableLiveData<List<String>?>()
    val choiceDistance = MutableLiveData<Double?>()


    fun navigateToResult(){
        navigateToResult.value = FilterParameter(
            choiceLevel.value ?: null,
            choiceCategory.value ?: null,
            choiceStyle.value ?: null,
            choiceDistance.value ?: null
        )
    }

    fun onLeft(){
        navigateToResult.value = null
    }
}
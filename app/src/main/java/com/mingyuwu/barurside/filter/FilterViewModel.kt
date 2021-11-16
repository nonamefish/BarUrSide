package com.mingyuwu.barurside.filter


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.source.BarUrSideRepository


class FilterViewModel() : ViewModel() {

    val navigateToResult = MutableLiveData<FilterParameter?>()
    val choiceLevel = MutableLiveData<Int?>()
    val choiceCategory = MutableLiveData<MutableList<String>?>()
    val choiceStyle = MutableLiveData<MutableList<String>?>()
    val choiceDistance = MutableLiveData<Double?>()

    fun isCheckChoiceCategory(type: String, isChecked: Boolean) {
        if (choiceCategory.value != null) {
            if (isChecked) {
                choiceCategory.value?.add(type)
            } else {
                choiceCategory.value?.removeIf { it == type }
            }
        } else {
            choiceCategory.value = mutableListOf(type)
        }
        choiceCategory.value = choiceCategory.value
    }

    fun isCheckChoiceStyle(type: String, isChecked: Boolean) {
        if (choiceStyle.value != null) {
            if (isChecked) {
                choiceStyle.value?.add(type)
            } else {
                choiceStyle.value?.removeIf { it == type }
            }
        } else {
            choiceStyle.value = mutableListOf(type)
        }
        choiceStyle.value = choiceStyle.value
    }


    fun isCheckChoiceDistance(distance: Double?, isChecked: Boolean) {
        if (isChecked) {
            choiceDistance.value = distance
        } else {
            if(choiceDistance.value==distance){
                choiceDistance.value = null
            }
        }
    }

    fun navigateToResult(){
        navigateToResult.value = FilterParameter(
            choiceLevel.value ?: null,
            choiceCategory.value ?: null,
            choiceStyle.value ?: null,
            choiceDistance.value ?: null
        )
    }
}
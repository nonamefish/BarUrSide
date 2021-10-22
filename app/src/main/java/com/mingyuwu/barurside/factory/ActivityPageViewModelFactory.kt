package com.mingyuwu.barurside.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.activity.ActivityPageViewModel
import com.mingyuwu.barurside.activity.ActivityTypeFilter
import com.mingyuwu.barurside.collect.CollectPageViewModel

@Suppress("UNCHECKED_CAST")
class ActivityPageViewModelFactory (
    private val type: ActivityTypeFilter
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ActivityPageViewModel::class.java) ->
                    ActivityPageViewModel(type)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
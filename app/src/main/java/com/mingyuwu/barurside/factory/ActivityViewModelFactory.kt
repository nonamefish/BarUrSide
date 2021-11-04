package com.mingyuwu.barurside.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.activity.dialog.ActivityDetailViewModel
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.source.BarUrSideRepository

@Suppress("UNCHECKED_CAST")
class ActivityViewModelFactory(
    private val repository: BarUrSideRepository,
    private val activity: Activity
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ActivityDetailViewModel::class.java) ->
                    ActivityDetailViewModel(repository, activity)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
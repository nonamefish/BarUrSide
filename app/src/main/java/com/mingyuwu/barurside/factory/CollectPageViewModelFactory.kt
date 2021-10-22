package com.mingyuwu.barurside.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.collect.CollectPageViewModel

@Suppress("UNCHECKED_CAST")
class CollectPageViewModelFactory(
    private val isVenue: Boolean
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(CollectPageViewModel::class.java) ->
                    CollectPageViewModel(isVenue)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
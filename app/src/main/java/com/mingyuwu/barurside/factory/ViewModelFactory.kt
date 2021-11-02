package com.mingyuwu.barurside.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.MainViewModel
import com.mingyuwu.barurside.addactivity.AddActivityViewModel
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.editrating.EditRatingViewModel
import com.mingyuwu.barurside.filter.FilterViewModel
import com.mingyuwu.barurside.map.MapViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: BarUrSideRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel()
                isAssignableFrom(AddActivityViewModel::class.java) ->
                    AddActivityViewModel()
                isAssignableFrom(FilterViewModel::class.java) ->
                    FilterViewModel()
                isAssignableFrom(MapViewModel::class.java) ->
                    MapViewModel(repository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}

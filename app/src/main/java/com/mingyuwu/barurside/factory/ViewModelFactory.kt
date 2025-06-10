package com.mingyuwu.barurside.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.MainViewModel
import com.mingyuwu.barurside.addactivity.AddActivityViewModel
import com.mingyuwu.barurside.addvenue.AddVenueViewModel
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.discover.DiscoverViewModel
import com.mingyuwu.barurside.filter.FilterViewModel
import com.mingyuwu.barurside.login.LoginViewModel
import com.mingyuwu.barurside.map.MapViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: BarUrSideRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(repository)
                isAssignableFrom(AddActivityViewModel::class.java) ->
                    AddActivityViewModel(repository)
                isAssignableFrom(FilterViewModel::class.java) ->
                    FilterViewModel()
                isAssignableFrom(MapViewModel::class.java) ->
                    MapViewModel(repository)
                isAssignableFrom(DiscoverViewModel::class.java) ->
                    DiscoverViewModel(repository)
                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(repository)
                isAssignableFrom(AddVenueViewModel::class.java) ->
                    AddVenueViewModel(repository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}

package com.mingyuwu.barurside.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.discoverdetail.DiscoverDetailViewModel
import com.mingyuwu.barurside.filter.FilterParameter
import com.mingyuwu.barurside.venue.VenueViewModel

@Suppress("UNCHECKED_CAST")
class VenueViewModelFactory(
    private val repository: BarUrSideRepository,
    private val id: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(VenueViewModel::class.java) ->
                    VenueViewModel(repository, id)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
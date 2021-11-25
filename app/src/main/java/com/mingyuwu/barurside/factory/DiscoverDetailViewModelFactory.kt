package com.mingyuwu.barurside.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.discoverdetail.DiscoverDetailViewModel
import com.mingyuwu.barurside.filter.FilterParameter

@Suppress("UNCHECKED_CAST")
class DiscoverDetailViewModelFactory(
    private val repository: BarUrSideRepository,
    private val id: List<String>?,
    private val theme: Theme,
    private val filterParameter: FilterParameter?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DiscoverDetailViewModel::class.java) ->
                    DiscoverDetailViewModel(repository, id, theme, filterParameter)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}

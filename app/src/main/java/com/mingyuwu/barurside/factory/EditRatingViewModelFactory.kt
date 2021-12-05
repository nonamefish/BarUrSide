package com.mingyuwu.barurside.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.editrating.EditRatingViewModel

@Suppress("UNCHECKED_CAST")
class EditRatingViewModelFactory(
    private val repository: BarUrSideRepository,
    private val venue: Venue
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(EditRatingViewModel::class.java) ->
                    EditRatingViewModel(repository, venue)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}

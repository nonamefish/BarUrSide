package com.mingyuwu.barurside.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.adddrink.AddDrinkViewModel
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.drink.DrinkViewModel
import com.mingyuwu.barurside.profile.ProfileViewModel
import com.mingyuwu.barurside.venue.VenueViewModel

@Suppress("UNCHECKED_CAST")
class InfoViewModelFactory(
    private val repository: BarUrSideRepository,
    private val id: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(VenueViewModel::class.java) ->
                    VenueViewModel(repository, id)
                isAssignableFrom(DrinkViewModel::class.java) ->
                    DrinkViewModel(repository, id)
                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(repository, id)
                isAssignableFrom(AddDrinkViewModel::class.java) ->
                    AddDrinkViewModel(repository, id)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}

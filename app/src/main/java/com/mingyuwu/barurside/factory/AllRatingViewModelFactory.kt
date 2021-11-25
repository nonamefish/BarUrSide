package com.mingyuwu.barurside.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.rating.all.AllRatingViewModel

@Suppress("UNCHECKED_CAST")
class AllRatingViewModelFactory(
    private val ratings: List<RatingInfo>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(AllRatingViewModel::class.java) ->
                    AllRatingViewModel(ratings)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}

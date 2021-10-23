package com.mingyuwu.barurside.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mingyuwu.barurside.addactivity.AddActivityViewModel
import com.mingyuwu.barurside.editrating.EditRatingViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(EditRatingViewModel::class.java) ->
                    EditRatingViewModel()

                isAssignableFrom(AddActivityViewModel::class.java) ->
                    AddActivityViewModel()

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}

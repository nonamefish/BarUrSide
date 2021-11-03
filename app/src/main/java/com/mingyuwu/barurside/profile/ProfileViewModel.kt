package com.mingyuwu.barurside.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.mockdata.RatingData
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.databinding.FragmentProfileBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.discoverdetail.DiscoverDetailFragmentArgs
import com.mingyuwu.barurside.rating.ImageAdapter
import com.mingyuwu.barurside.rating.UserRatingAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ProfileViewModel(private val repository: BarUrSideRepository, val userId: String) : ViewModel() {

    // set source data
    var userInfo = MutableLiveData<Drink>()
    var rtgInfo = MutableLiveData<List<RatingInfo>>()
    val userId_test = "6BhbnIMi1Ai91Ky4w9rI"

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

    }

    fun getUserInfo(userId:String){
        coroutineScope.launch {
//            val result = repository.getCollect(userId)
//            isCollect.value = when (result) {
//                is Result.Success -> {
//                    _error.value = null
//                    result.data.any { it.objectId == id }
//                }
//                is Result.Fail -> {
//                    _error.value = result.error
//                    null
//                }
//                is Result.Error -> {
//                    _error.value = result.exception.toString()
//                    null
//                }
//                else -> {
//                    null
//                }
//            }
        }
    }

    fun getRatingInfo(userId:String){
        coroutineScope.launch {
//            val result = repository.getCollect(userId)
//            isCollect.value = when (result) {
//                is Result.Success -> {
//                    _error.value = null
//                    result.data.any { it.objectId == id }
//                }
//                is Result.Fail -> {
//                    _error.value = result.error
//                    null
//                }
//                is Result.Error -> {
//                    _error.value = result.exception.toString()
//                    null
//                }
//                else -> {
//                    null
//                }
//            }
        }
    }

}
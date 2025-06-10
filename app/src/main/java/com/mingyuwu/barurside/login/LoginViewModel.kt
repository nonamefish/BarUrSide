package com.mingyuwu.barurside.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: BarUrSideRepository) : ViewModel() {

    // navigate to activity detail
    val navigateToDetail = MutableLiveData<Boolean?>()

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun onLeft() {
        navigateToDetail.value = null
    }

    fun addUser(user: User) {

        coroutineScope.launch {
            val result = repository.addUser(user)
            when (result) {
                is Result.Success -> {
                    _error.value = null
                }
                is Result.Fail -> {
                    _error.value = result.error
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                }
                else -> {
                    Logger.w("Wrong Result Type: $result")
                }
            }

            navigateToDetail.value = true
        }
    }

    fun getUserData(userId: String) {
        UserManager.user = repository.getUser(userId)
    }
}

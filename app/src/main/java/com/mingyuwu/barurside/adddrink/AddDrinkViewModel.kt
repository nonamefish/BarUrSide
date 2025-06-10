package com.mingyuwu.barurside.adddrink

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddDrinkViewModel(private val repository: BarUrSideRepository, val id: String) :
    ViewModel() {

    // drink information
    private val userId = UserManager.user.value?.id
    var venue = MutableLiveData<Venue>()
    val name = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val price = MutableLiveData<String>()

    // image: The internal MutableLiveData that stores the image(Bitmap)
    private val _image = MutableLiveData<Bitmap?>()
    val image: LiveData<Bitmap?>
        get() = _image

    // imageUrl: The internal MutableLiveData that stores the imageUrl
    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?>
        get() = _imageUrl

    // after send drink info to firebase
    private var _leave = MutableLiveData<Boolean?>()

    val leave: LiveData<Boolean?>
        get() = _leave

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        venue = repository.getVenue(id)
    }

    fun addDrink() {

        coroutineScope.launch {

            val drink = Drink(
                "",
                venue.value?.menuId ?: "",
                venue.value?.id ?: "",
                name.value ?: "",
                type.value ?: "",
                price.value?.toLongOrNull() ?: 0,
                description.value ?: "",
                imageUrl.value?.let { listOf(it) },
                0.0,
                0
            )

            repository.addDrink(drink)

            _leave.value = true
        }
    }

    fun onLeft() {
        _leave.value = null
    }

    fun addUploadImg(bitmap: Bitmap, url: String) {
        _image.value = bitmap
        _imageUrl.value = url
    }

    fun checkValue(): Boolean {
        if (name.value == null ||
            price.value == null ||
            type.value == null
        ) {
            return false
        }

        return true
    }

    fun uploadPhoto() {

        val storage = Firebase.storage
        val storageRef = storage.reference

        coroutineScope.launch {

            imageUrl.value?.let {
                val result = repository.uploadPhoto(storageRef, userId ?: "", "drink", it)
                when (result) {
                    is Result.Success -> {
                        _error.value = null
                        _imageUrl.value = result.data
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        null
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        null
                    }
                    else -> {
                        null
                    }
                }
            }

            addDrink()
        }
    }
}

package com.mingyuwu.barurside.addvenue

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddVenueViewModel(private val repository: BarUrSideRepository) :
    ViewModel() {

    // venue information
    private val userId = UserManager.user.value?.id
    val name = MutableLiveData<String>()
    var address = MutableLiveData<String>()
    var latitude = MutableLiveData<Double>()
    var longitude = MutableLiveData<Double>()
    val level = MutableLiveData<Int>()
    val phone = MutableLiveData<String>()
    val web = MutableLiveData<String>()
    val style = MutableLiveData<String>()
    val startTime = MutableLiveData<String>()
    val closeTime = MutableLiveData<String>()

    // image: The internal MutableLiveData that stores the image(Bitmap)
    private val _image = MutableLiveData<Bitmap?>()
    val image: LiveData<Bitmap?>
        get() = _image

    // imageUrl: The internal MutableLiveData that stores the imageUrl
    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?>
        get() = _imageUrl

    // after send venue info to firebase then navigate to discover fragment
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

    fun addVenue() {
        coroutineScope.launch {

            val venue = Venue(
                "",
                "",
                name.value!!,
                style.value!!,
                level.value!!.toLong(),
                "${startTime.value!!}-${closeTime.value!!}",
                web.value ?: "",
                phone.value ?: "",
                address.value!!,
                latitude.value!!,
                longitude.value!!,
                imageUrl.value?.let { listOf(it) },
                0.0,
                0
            )

            repository.addVenue(venue)

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
            address.value == null ||
            startTime.value == null ||
            closeTime.value == null ||
            level.value == null ||
            style.value == null
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
                val result = repository.uploadPhoto(storageRef, userId ?: "", "venue", it)
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

            addVenue()
        }
    }
}

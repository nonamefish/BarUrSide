package com.mingyuwu.barurside.addobject

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class AddObjectViewModel(private val repository: BarUrSideRepository, val id: String) :
    ViewModel() {

    // activity information
    val venue = MutableLiveData<Venue?>()
    val name = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val type = MutableLiveData<String>()

    val price = MutableLiveData<String>()
    val numPrice: LiveData<Int>
        get() = price.map { it.toIntOrNull() ?: 0 }

    private val _image = MutableLiveData<Bitmap?>()
    val image: LiveData<Bitmap?>
        get() = _image

    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?>
        get() = _imageUrl

    // navigate to Venue
    val navigateToDetail = MutableLiveData<Boolean?>()

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    private fun convertStringToTimestamp(time: String): Timestamp {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd  a HH:mm", Locale.TAIWAN)
        val parsedDate = dateFormat.parse(time)
        return Timestamp(parsedDate.time)
    }

    init{
        venue.value = repository.getVenue(id).value
    }


    fun addDrink() {
        coroutineScope.launch {
            val drink = Drink(
                "",
                venue.value?.menuId ?: "",
                venue.value?.id ?: "",
                name.value!!,
                type.value ?: "",
                price.value?.toLong() ?: 0,
                listOf(imageUrl.value ?: ""),
                0.0,
                0
            )
            repository.addDrink(drink)
            navigateToDetail.value = true
        }
    }

    fun onLeft() {
        navigateToDetail.value = null
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
}
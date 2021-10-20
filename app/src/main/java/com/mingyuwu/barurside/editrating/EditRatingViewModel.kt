package com.mingyuwu.barurside.editrating

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditRatingViewModel() : ViewModel() {

    private val _star = MutableLiveData<MutableList<Int?>>(listOf(null).toMutableList())
    val star: LiveData<MutableList<Int?>>
        get() = _star

    private val _comment = MutableLiveData<MutableList<String?>>(listOf(null).toMutableList())
    val comment: LiveData<MutableList<String?>>
        get() = _comment

    private val _uploadImg = MutableLiveData<MutableList<Bitmap?>>(listOf(null).toMutableList())
    val uploadImg: LiveData<MutableList<Bitmap?>>
        get() = _uploadImg

    private val _tagFrd = MutableLiveData<MutableList<String?>>(listOf(null).toMutableList())
    val tagFrd: LiveData<MutableList<String?>>
        get() = _tagFrd


    // all rating item
    private val _rtgList = MutableLiveData<MutableList<String>>()
    val rtgList: LiveData<MutableList<String>>
        get() = _rtgList

    // check rating lbject is venue or drink
    private val _isVenue = MutableLiveData<MutableList<Boolean>>(listOf(true).toMutableList())
    val isVenue: LiveData<MutableList<Boolean>>
        get() = _isVenue


    init {
        _rtgList.value = listOf("1").toMutableList()
    }

    fun clickRatingStore(score: Int, rtgOrder: Int) {
        if (_star.value == null) {
            _star.value = listOf(1).toMutableList()
        } else {
            _star.value!![rtgOrder] = score
            _star.value = _star.value
        }

        Log.d("Ming", "Fragment_star.value: ${_star.value}")
    }

    fun addNewRating() {
        _isVenue.value!! += listOf(false).toMutableList()
        _rtgList.value!! += listOf("2").toMutableList()
        _star.value!! += listOf(null).toMutableList()
        _uploadImg.value!! += listOf(null).toMutableList()
        _comment.value!! += listOf(null).toMutableList()
        _tagFrd.value!! += listOf(null).toMutableList()
        _rtgList.value = _rtgList.value
    }


    fun addUploadImg(position: Int, img: Bitmap?) {
        _uploadImg.value!![position] = img
    }

    fun addTagFrd(position: Int, frdId: String) {
        _tagFrd.value!![position] = frdId
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun logLargeString(str: String) {
        if (str.length > 3000) {
            Log.i("Ming", str.substring(0, 3000))
            logLargeString(str.substring(3000))
        } else {
            Log.i("Ming", str) // continuation
        }
    }

}
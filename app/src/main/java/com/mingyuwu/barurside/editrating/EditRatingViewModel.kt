package com.mingyuwu.barurside.editrating

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp

class EditRatingViewModel(val repository: BarUrSideRepository, private val venue: Venue) :
    ViewModel() {

    private val userId = "6BhbnIMi1Ai91Ky4w9rI"

    private val _star = MutableLiveData<MutableList<Int?>>()
    val star: LiveData<MutableList<Int?>>
        get() = _star

    private val _comment = MutableLiveData<MutableList<String?>>()
    val comment: LiveData<MutableList<String?>>
        get() = _comment

    private val _uploadImg = MutableLiveData<MutableList<MutableList<Bitmap?>>>()
    val uploadImg: LiveData<MutableList<MutableList<Bitmap?>>>
        get() = _uploadImg

    private val _uploadImgUrl = MutableLiveData<MutableList<MutableList<String?>>>()
    val uploadImgUrl: LiveData<MutableList<MutableList<String?>>>
        get() = _uploadImgUrl

    private val _firebaseImgUrl = MutableLiveData<MutableList<MutableList<String>>>()
    val firebaseImgUrl: LiveData<MutableList<MutableList<String>>>
        get() = _firebaseImgUrl

    private val _tagFrd = MutableLiveData<MutableList<String>?>(null)
    val tagFrd: LiveData<MutableList<String>?>
        get() = _tagFrd

    // control upload image
    val isUploadImgBtn = MutableLiveData<Boolean>()
    val clickPosition = MutableLiveData<Int>()

    // all user item
    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    // all rating item
    private val _frdList = MutableLiveData<List<User>>()
    val frdList: LiveData<List<User>>
        get() = _frdList

    // menu: all drink item
    private val _menu = MutableLiveData<MutableList<Drink>>()
    val menu: LiveData<MutableList<Drink>>
        get() = _menu

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // check rating object is venue or drink
    private val _objectId = MutableLiveData<MutableList<String>>()
    val objectId: LiveData<MutableList<String>>
        get() = _objectId

    // check rating object is venue or drink
    private val _objectName = MutableLiveData<MutableList<String>>()
    val objectName: LiveData<MutableList<String>>
        get() = _objectName

    // after set rating
    private val _leave = MutableLiveData<Boolean?>()

    val leave: LiveData<Boolean?>
        get() = _leave

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        getUser(userId)
        getMenu(venue.id)
        _objectId.value = mutableListOf(venue.id)
        _objectName.value = mutableListOf(venue.name)
        _uploadImg.value = mutableListOf(mutableListOf(null))
        _uploadImgUrl.value = mutableListOf(mutableListOf(null))
        _comment.value = mutableListOf(null)
        _star.value = mutableListOf(null)
    }

    fun clickRatingStore(score: Int, rtgOrder: Int) {
        if (_star.value == null) {
            _star.value = listOf(1).toMutableList()
        } else {
            _star.value!![rtgOrder] = score
            _star.value = _star.value
        }
    }

    fun addNewRating(drink: Drink) {
        _objectId.value!!.add(drink.id)
        _objectName.value!!.add(drink.name)
        _star.value!!.add(null)
        _uploadImg.value!!.add(mutableListOf(null))
        _uploadImgUrl.value!!.add(mutableListOf(null))
        _comment.value!!.add(null)
        _objectId.value = _objectId.value
    }

    fun addUploadImg(position: Int, bitmap: Bitmap?, url: String) {


        if (_uploadImg.value!![position][0] == null) {
            _uploadImg.value!![position] = mutableListOf(bitmap)
            _uploadImgUrl.value!![position] = mutableListOf(url)
        } else {
            _uploadImg.value!![position].add(bitmap)
            _uploadImgUrl.value!![position].add(url)
        }
        _uploadImg.value = _uploadImg.value
        _uploadImgUrl.value = _uploadImgUrl.value
        Log.d("Ming", "url: ${_uploadImg.value}")
    }

    fun removeUploadImg(rtgOrder: Int, position: Int) {

        _uploadImg.value?.get(rtgOrder)?.removeAt(position)
        _uploadImgUrl.value?.get(rtgOrder)?.removeAt(position)

        _uploadImg.value = _uploadImg.value
        _uploadImgUrl.value = _uploadImgUrl.value
        Log.d("Ming", "url: ${_uploadImg.value}")
    }


    fun addTagFrd(position: Int, frdId: String) {
        if (_tagFrd.value == null) {
            _tagFrd.value = mutableListOf(frdId)
        } else {
            _tagFrd.value!!.add(frdId)
        }
        _tagFrd.value = _tagFrd.value
    }

    fun postRating() {
        coroutineScope.launch {
            objectId.value?.let {
                var addShare = 0
                var addShareImg = 0

                for (index in 0 until objectId.value!!.size) {

                    val rtg = Rating(
                        "",
                        objectId.value!![index],
                        index == 0,
                        userId,
                        star.value!![index]!!.toLong(),
                        comment.value!![index] ?: "",
                        _firebaseImgUrl?.value?.get(index) ?: null,
                        Timestamp(System.currentTimeMillis()),
                        tagFrd.value
                    )
                    addShare += 1
                    addShareImg += _firebaseImgUrl?.value?.get(index)?.size ?: 0
                    repository.postRating(rtg)
                    updateObjectRating(objectId.value!![index], index == 0, rtg)
                }
                updateUserShare(addShare, addShareImg)
            }
            leave()
        }
    }

    fun uploadPhoto() {
        val storage = Firebase.storage
        val storageRef = storage.reference

        coroutineScope.launch {
            val imgs = mutableListOf<String>()
            for (index in 0 until objectId.value!!.size) {

                val type = when (index) {
                    0 -> "venue"
                    else -> "drink"
                }

                _uploadImgUrl.value?.get(index).let {

                    it?.forEachIndexed { listIndex, url ->

                        url?.let {
                            when (val result =
                                repository.uploadPhoto(storageRef, userId, type, url)) {
                                is Result.Success -> {
                                    Log.d("Ming", "_uploadImgUrl: ${result.data}")
                                    _error.value = null
                                    imgs.add(listIndex, result.data)
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
                    }
                    if (_firebaseImgUrl.value == null) {
                        _firebaseImgUrl.value = mutableListOf(imgs)
                    } else {
                        _firebaseImgUrl.value!!.add(imgs)
                    }

                }
            }
            postRating()
        }
    }

    private fun getUser(id: String) {
        _user = repository.getUser(id)
    }

    fun getFriendList(user: User) {
        coroutineScope.launch {

            val result = repository.getFriend(user)
            _frdList.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    result.data
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
    }

    private fun getMenu(venueId: String) {
        coroutineScope.launch {

            val result = repository.getMenu(venueId)
            _menu.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    result.data.toMutableList()
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
    }

    fun removeMenuItem(position: Int) {
        _menu.value?.removeAt(position)
        _menu.value = _menu.value
    }

    fun leave() {
        _leave.value = true
    }

    fun onLeft() {
        _leave.value = null
    }

    private fun updateObjectRating(
        id: String,
        isVenue: Boolean,
        rating: Rating
    ) {
        coroutineScope.launch {
            repository.updateRating(id, isVenue, rating)
        }
    }

    private fun updateUserShare(
        addShareCnt: Int,
        addShareImgCnt: Int
    ) {
        coroutineScope.launch {
            repository.updateUserShare(userId, addShareCnt, addShareImgCnt)
        }
    }

    fun checkRating(): Boolean {
        _objectId?.value?.forEachIndexed { index, s ->
            if (_star.value?.get(index) == null) {
                return false
            }
        }
        return true
    }

    fun removeRating(rtgOrder: Int) {
        _menu.value?.add(
            Drink(
                id = _objectId.value!![rtgOrder],
                name = _objectName.value!![rtgOrder]
            )
        )
        _objectId.value!!.removeAt(rtgOrder)
        _objectName.value!!.removeAt(rtgOrder)
        _star.value?.removeAt(rtgOrder)
        _uploadImg.value?.removeAt(rtgOrder)
        _uploadImgUrl.value?.removeAt(rtgOrder)
        _comment.value?.removeAt(rtgOrder)
        _objectId.value = _objectId.value
        _menu.value = _menu.value
    }
}
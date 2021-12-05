package com.mingyuwu.barurside.editrating

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.util.Logger
import java.sql.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditRatingViewModel(val repository: BarUrSideRepository, venue: Venue) :
    ViewModel() {

    private val userId = UserManager.user.value?.id

    // star: The internal MutableLiveData that stores rating scores
    private val _star = MutableLiveData<MutableList<Int?>>()
    val star: LiveData<MutableList<Int?>>
        get() = _star

    // comment: The internal MutableLiveData that rating comments
    private val _comment = MutableLiveData<MutableList<String?>>()
    val comment: LiveData<MutableList<String?>>
        get() = _comment

    // uploadImg: The internal MutableLiveData that stores rating bitmap images
    private val _uploadImg = MutableLiveData<MutableList<MutableList<Bitmap?>>>()
    val uploadImg: LiveData<MutableList<MutableList<Bitmap?>>>
        get() = _uploadImg

    // uploadImgUrl: The internal MutableLiveData that stores rating image client file urls
    private val _uploadImgUrl = MutableLiveData<MutableList<MutableList<String?>>>()

    // firebaseImgUrl: The internal MutableLiveData that stores rating image firebase urls
    private val _firebaseImgUrl = MutableLiveData<MutableList<MutableList<String>>>()

    // tagFrd: The internal MutableLiveData that stores rating tag friends
    private val _tagFrd = MutableLiveData<MutableList<TagFriend>?>(null)
    val tagFrd: LiveData<MutableList<TagFriend>?>
        get() = _tagFrd

    // control upload image
    val isUploadImgBtn = MutableLiveData<Boolean>()
    val clickPosition = MutableLiveData<Int>()

    // user: user information
    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    // frdList: The internal MutableLiveData that stores friends
    private val _frdList = MutableLiveData<List<User>>()
    val frdList: LiveData<List<User>>
        get() = _frdList

    // menu: The internal MutableLiveData that stores drink items
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
        getUser()
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
    }

    fun removeUploadImg(rtgOrder: Int, position: Int) {

        _uploadImg.value?.get(rtgOrder)?.removeAt(position)
        _uploadImgUrl.value?.get(rtgOrder)?.removeAt(position)

        _uploadImg.value = _uploadImg.value
        _uploadImgUrl.value = _uploadImgUrl.value
    }

    fun addTagFrd(frdId: TagFriend) {

        if (_tagFrd.value == null) {
            _tagFrd.value = mutableListOf(frdId)
        } else {
            _tagFrd.value!!.add(frdId)
        }

        _tagFrd.value = _tagFrd.value
    }

    private fun postRating() {

        coroutineScope.launch {

            objectId.value?.let {

                var addShare = 0
                var addShareImg = 0

                for (index in 0 until objectId.value!!.size) {

                    val rtg = Rating(
                        "",
                        objectId.value!![index],
                        index == 0,
                        userId ?: "",
                        star.value!![index]!!.toLong(),
                        comment.value!![index] ?: "",
                        _firebaseImgUrl.value?.get(index),
                        Timestamp(System.currentTimeMillis()),
                        tagFrd.value
                    )

                    addShare += 1
                    addShareImg += _firebaseImgUrl.value?.get(index)?.size ?: 0
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
                            when (
                                val result =
                                    repository.uploadPhoto(storageRef, userId ?: "", type, url)
                            ) {
                                is Result.Success -> {
                                    _error.value = null
                                    imgs.add(listIndex, result.data)
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

    private fun getUser() {
        _user = UserManager.user
    }

    fun getUsersResultList(user: User) {
        coroutineScope.launch {
            user.friends?.let {
                val result = repository.getUsersResult(user.friends.map { it.id })
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

    private fun leave() {
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
            userId?.let {
                repository.updateUserShare(userId, addShareCnt, addShareImgCnt)
            }
        }
    }

    fun checkRating(): Boolean {

        _objectId.value?.forEachIndexed { index, _ ->
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

package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.StorageReference
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.Venue

interface BarUrSideRepository {
    fun getVenue(id: String): MutableLiveData<Venue>

    fun getRating(id: String, isVenue: Boolean): MutableLiveData<List<Rating>>

    fun getDrink(id: String): MutableLiveData<Drink>

    fun getUser(id: String): MutableLiveData<User>

    suspend fun postRating(rating: Rating): Result<Boolean>

    suspend fun uploadPhoto(storageRef: StorageReference, type: String, localImage: String)

    suspend fun getFriend(user: User): Result<List<User>>

    suspend fun getMenu(venueId: String): Result<List<Drink>>
}
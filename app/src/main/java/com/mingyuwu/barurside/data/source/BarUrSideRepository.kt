package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.StorageReference
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.filter.FilterParameter

interface BarUrSideRepository {
    fun getVenue(id: String): MutableLiveData<Venue>

    fun getRating(id: String, isVenue: Boolean): MutableLiveData<List<RatingInfo>>

    fun getDrink(id: String): MutableLiveData<Drink>

    fun getUser(id: String): MutableLiveData<User>

    suspend fun postRating(rating: Rating): Result<Boolean>

    suspend fun uploadPhoto(
        storageRef: StorageReference,
        userId: String,
        type: String,
        localImage: String
    ): Result<String>

    suspend fun getFriend(user: User): Result<List<User>>

    suspend fun getMenu(venueId: String): Result<List<Drink>>

    suspend fun updateRating(id: String, isVenue: Boolean, rating: Rating): Result<Boolean>

    suspend fun updateUserShare(
        userId: String,
        addShareCnt: Int,
        addShareImgCnt: Int
    ): Result<Boolean>

    suspend fun getVenueByFilter(filter: FilterParameter): Result<List<Venue>>
    suspend fun getVenueByLocation(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Result<List<Venue>>
    suspend fun getDrinkByRating(id: String): Result<Drink>
    suspend fun getVenueBySearch(search: String): Result<List<Venue>>
    suspend fun getHotVenueResult(): Result<List<Venue>>
    suspend fun getHotDrinkResult(): Result<List<Drink>>
    suspend fun getHighRateVenueResult(): Result<List<Venue>>
    suspend fun getHighRateDrinkResult(): Result<List<Drink>>
    suspend fun getActivityResult() : Result<List<Activity>>
}
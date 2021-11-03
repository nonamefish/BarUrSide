package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.StorageReference
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.filter.FilterParameter

interface BarUrSideDataSource {
    fun getVenue(id: String): MutableLiveData<Venue>

    fun getRatingByObject(id: String, isVenue: Boolean): MutableLiveData<List<RatingInfo>>

    fun getDrink(id: String): MutableLiveData<Drink>

    fun getUser(id: String): MutableLiveData<User>

    suspend fun postRating(rating: Rating): Result<Boolean>

    suspend fun uploadPhoto(
        storageRef: StorageReference,
        userId: String,
        type: String,
        localImage: String
    ): Result<String>

    suspend fun getFriend(frds: List<String>): Result<List<User>>

    suspend fun getMenu(venueId: String): Result<List<Drink>>

    suspend fun updateObjectRating(id: String, isVenue: Boolean, rating: Rating): Result<Boolean>

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
    suspend fun getDrinkBySearch(search: String): Result<List<Drink>>
    suspend fun postCollect(collect: Collect): Result<Boolean>
    suspend fun getCollect(userId: String): Result<List<Collect>>
    suspend fun removeCollect(id: String, userId: String): Result<Boolean>
    suspend fun getVenueByIds(ids: List<String>): Result<List<Venue>>
    suspend fun getDrinksByIds(ids: List<String>): Result<List<Drink>>
    suspend fun getRatingByUser(userId: String): Result<List<RatingInfo>>
    suspend fun getActivityByUser(userId: String): Result<List<Activity>>
}
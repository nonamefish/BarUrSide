package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.StorageReference
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.filter.FilterParameter

class DefaultBarUrSideRepository(
    private val remoteDataSource: BarUrSideDataSource,
    private val localDataSource: BarUrSideDataSource
) : BarUrSideRepository {

    override fun getVenue(id: String): MutableLiveData<Venue> {
        return remoteDataSource.getVenue(id)
    }

    override fun getRating(id: String, isVenue: Boolean): MutableLiveData<List<RatingInfo>> {
        return remoteDataSource.getRating(id, isVenue)
    }

    override fun getDrink(id: String): MutableLiveData<Drink> {
        return remoteDataSource.getDrink(id)
    }

    override fun getUser(id: String): MutableLiveData<User> {
        return remoteDataSource.getUser(id)
    }

    override suspend fun postRating(rating: Rating): Result<Boolean> {
        return remoteDataSource.postRating(rating)
    }

    override suspend fun uploadPhoto(
        storageRef: StorageReference,
        userId: String,
        type: String,
        localImage: String
    ) {
        return remoteDataSource.uploadPhoto(storageRef, userId, type, localImage)
    }

    override suspend fun getFriend(user: User): Result<List<User>> {
        return remoteDataSource.getFriend(user)
    }

    override suspend fun getMenu(venueId: String): Result<List<Drink>> {
        return remoteDataSource.getMenu(venueId)
    }

    override suspend fun updateRating(
        id: String,
        isVenue: Boolean,
        rating: Rating
    ): Result<Boolean> {
        return remoteDataSource.updateObjectRating(id, isVenue, rating)
    }

    override suspend fun updateUserShare(
        userId: String,
        addShareCnt: Int,
        addShareImgCnt: Int
    ): Result<Boolean> {
        return remoteDataSource.updateUserShare(userId, addShareCnt, addShareImgCnt)
    }

    override suspend fun getVenueByFilter(filter: FilterParameter): Result<List<Venue>> {
        return remoteDataSource.getVenueByFilter(filter)
    }

    override suspend fun getVenueByRating(id: String): Result<Venue> {
        return remoteDataSource.getVenueByRating(id)
    }

    override suspend fun getDrinkByRating(id: String): Result<Drink> {
        return remoteDataSource.getDrinkByRating(id)
    }
}